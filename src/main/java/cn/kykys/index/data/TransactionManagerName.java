package cn.kykys.index.data;

/**
 * TransactionManagerName
 *
 *  事务管理器,增加一个数据库必须在此定义,且和配置文件中定义的管理器名称一致
 */
public class TransactionManagerName {

    /**
     * 事务管理器
     */
    public final static String kykysDbTransactionManager ="kykysDbTransactionManager";

    public final static String wechatDbTransactionManager ="wechatDbTransactionManager";
}
