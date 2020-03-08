package cmd.lzx.main;

/**
 * @author 1Zx.
 * @date 2020/3/7 11:02
 */
public interface Constants {

    public static final Integer version = 1;

    public static final String ConfigURL = "https://raw.githubusercontent.com/1Zxuan/jxfsxcp/master/src/main/resources/config.properties";

    public static final String AppStatic = "app.static";

    public static final String AppVersion = "app.version";

    public static final String COMMAND = "app.command";

    //标记目标邮件
    public static final String EMAILRECEIVEURL = "receive.email.url";

    public static final String EMAILFROMURL = "from.email.url";

    //邮箱类型
    public static final String EMAILTYPE = "email.type";

    //邮箱连接协议
    public static final String EMAILPROTOCOL = "email.protocol";

    public static final String EMAILAUTH = "email.auth";

    //邮箱主机名
    public static final String EMAILHOST = "email.host";

    //邮箱端口号
    public static final String EMAILPORT = "email.port";

    public static final String AuthorizationCode = "email.authorizationCode";

    public static final String EMAILSSLENABLE = "email.smtp.ssl.enable";

    public static final String EMAILDEBUG="email.debug";

    public static final String PUBLIC_IP = "public.ip";

    public static final String RESULTFILESIZE = "result.file.size";

}
