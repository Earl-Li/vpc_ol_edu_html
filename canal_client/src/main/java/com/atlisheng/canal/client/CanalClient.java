package com.atlisheng.canal.client;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry.*;
import com.alibaba.otter.canal.protocol.Message;
import com.atlisheng.canal.constants.ConstantsProperties;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Earl
 * @version 1.0.0
 * @描述 canal客户端类
 * @创建日期 2023/10/15
 * @since 1.0.0
 */
@Component
public class CanalClient {
    //sql队列
    private Queue<String> SQL_QUEUE = new ConcurrentLinkedQueue<>();
    //数据源
    @Resource
    private DataSource dataSource;

    /**
     * @描述 canal入库方法
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/15
     * @since 1.0.0
     */
    public void run() {
        CanalConnector connector = CanalConnectors.newSingleConnector(
                new InetSocketAddress("192.168.200.132", 11111),
                "example", "", "");
        //这里似乎不能用常量来设置参数，会导致实例无法创建服务启动不起来，经过测试确实只要有一个自动配置的常量都启动不起来，创建不了实例
        // 注意从这里访问canal不需要用户名和密码，似乎是直接访问的那边的Canal
        /*CanalConnector connector = CanalConnectors.newSingleConnector(new
                InetSocketAddress("192.168.200.132",
                11111), "example", "", "");*/
        int batchSize = 1000;
        try {
            connector.connect();
            connector.subscribe(".*\\..*");
            connector.rollback();
            try {
                while (true) {
                    //尝试从master那边拉去数据batchSize条记录，有多少取多少
                    Message message = connector.getWithoutAck(batchSize);
                    long batchId = message.getId();
                    //1. size的值为0，线程睡1s，否则size大于0表示数据库变化了，执行dataHandle方法
                    int size = message.getEntries().size();
                    if (batchId == -1 || size == 0) {
                        Thread.sleep(1000);
                    } else {
                        //2. 在dataHandle中判断数据库更改类型，根据远程数据的变化获取对应数据拼接sql语句存入sql队列中
                        dataHandle(message.getEntries());
                    }

                    connector.ack(batchId);
                    //3. 当队列里面堆积的sql大于一定数值的时候就模拟执行，在executeQueueSql方法中用数据源把语句进行执行，一直在执行，一秒一次
                    // 没有变化，就休眠。有变化就拼接sql放入队列执行？一次更新多条是放在一起拼接吗，对是多条，message.getEntries()
                    //是一个list集合，遍历完才会跳出判断类型拼接sql，加入队列的操作
                    if (SQL_QUEUE.size() >= 1) {
                        executeQueueSql();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        } finally {
            connector.disconnect();
        }
    }

    /**
     * @描述 模拟执行队列里面的sql语句
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/15
     * @since 1.0.0
     */
    public void executeQueueSql() {
        int size = SQL_QUEUE.size();
        for (int i = 0; i < size; i++) {
            String sql = SQL_QUEUE.poll();
            System.out.println("[sql]----> " + sql);
            this.execute(sql.toString());
        }
    }

    /**
     * @param entrys
     * @描述 数据处理
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/15
     * @since 1.0.0
     */
    private void dataHandle(List<Entry> entrys) throws InvalidProtocolBufferException {
        for (Entry entry : entrys) {
            if (EntryType.ROWDATA == entry.getEntryType()) {
                RowChange rowChange = RowChange.parseFrom(entry.getStoreValue());
                EventType eventType = rowChange.getEventType();
                //如果是XX操作执行对应的saveXXSql(entry)方法
                if (eventType == EventType.DELETE) {
                    saveDeleteSql(entry);
                } else if (eventType == EventType.UPDATE) {
                    saveUpdateSql(entry);
                } else if (eventType == EventType.INSERT) {
                    saveInsertSql(entry);
                }
            }
        }
    }

    /**
     * @param entry
     * @描述 保存更新语句
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/15
     * @since 1.0.0
     */
    private void saveUpdateSql(Entry entry) {
        try {
            RowChange rowChange = RowChange.parseFrom(entry.getStoreValue());
            List<RowData> rowDatasList = rowChange.getRowDatasList();
            for (RowData rowData : rowDatasList) {
                List<Column> newColumnList = rowData.getAfterColumnsList();
                StringBuffer sql = new StringBuffer("update " +
                        entry.getHeader().getTableName() + " set ");
                for (int i = 0; i < newColumnList.size(); i++) {
                    sql.append(" " + newColumnList.get(i).getName()
                            + " = '" + newColumnList.get(i).getValue() + "'");
                    if (i != newColumnList.size() - 1) {
                        sql.append(",");
                    }
                }
                sql.append(" where ");
                List<Column> oldColumnList = rowData.getBeforeColumnsList();
                for (Column column : oldColumnList) {
                    if (column.getIsKey()) {
                        //暂时只支持单一主键
                        sql.append(column.getName() + "=" + column.getValue());
                        break;
                    }
                }
                SQL_QUEUE.add(sql.toString());
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param entry
     * @描述 保存删除语句
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/15
     * @since 1.0.0
     */
    private void saveDeleteSql(Entry entry) {
        try {
            RowChange rowChange = RowChange.parseFrom(entry.getStoreValue());
            List<RowData> rowDatasList = rowChange.getRowDatasList();
            for (RowData rowData : rowDatasList) {
                List<Column> columnList = rowData.getBeforeColumnsList();
                StringBuffer sql = new StringBuffer("delete from " +
                        entry.getHeader().getTableName() + " where ");
                for (Column column : columnList) {
                    if (column.getIsKey()) {
                        //暂时只支持单一主键
                        sql.append(column.getName() + "=" + column.getValue());
                        break;
                    }
                }
                SQL_QUEUE.add(sql.toString());
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param entry
     * @描述 保存插入语句，获取到远程库中改了那个表或者字段，加了什么值，将这些信息提取出来拼接成sql语句，然后将语句放在队列
     * SQL_QUEUE中
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/15
     * @since 1.0.0
     */
    private void saveInsertSql(Entry entry) {
        try {
            RowChange rowChange = RowChange.parseFrom(entry.getStoreValue());
            List<RowData> rowDatasList = rowChange.getRowDatasList();
            for (RowData rowData : rowDatasList) {
                List<Column> columnList = rowData.getAfterColumnsList();
                StringBuffer sql = new StringBuffer("insert into " +
                        entry.getHeader().getTableName() + " (");
                for (int i = 0; i < columnList.size(); i++) {
                    sql.append(columnList.get(i).getName());
                    if (i != columnList.size() - 1) {
                        sql.append(",");
                    }
                }
                sql.append(") VALUES (");
                for (int i = 0; i < columnList.size(); i++) {
                    sql.append("'" + columnList.get(i).getValue() + "'");
                    if (i != columnList.size() - 1) {
                        sql.append(",");
                    }
                }
                sql.append(")");
                SQL_QUEUE.add(sql.toString());
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param sql
     * @描述 入库
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/15
     * @since 1.0.0
     */
    public void execute(String sql) {
        Connection con = null;
        try {
            if (null == sql) return;
            con = dataSource.getConnection();
            QueryRunner qr = new QueryRunner();
            int row = qr.execute(con, sql);
            System.out.println("update: " + row);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DbUtils.closeQuietly(con);
        }
    }
}


