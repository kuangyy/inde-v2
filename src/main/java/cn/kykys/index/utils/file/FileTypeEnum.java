package cn.kykys.index.utils.file;

/**
 * Created by kuangye on 2016/10/20.
 */
public enum FileTypeEnum {


    EXE(".exe","7790","可执行文件"),
    RAR(".rar","52617221","压缩文件"),


    ;


    FileTypeEnum(String suffix, String bytePrefix, String description) {
        this.suffix = suffix;
        this.bytePrefix = bytePrefix;
        this.description = description;
    }

    //后缀
    private String suffix;
    // byte前缀
    private String bytePrefix;
    //描述
    private String description;


    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getBytePrefix() {
        return bytePrefix;
    }

    public void setBytePrefix(String bytePrefix) {
        this.bytePrefix = bytePrefix;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
