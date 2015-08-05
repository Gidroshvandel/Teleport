package itlab.teleport;

/**
 * Created by Gidro on 05.08.2015.
 */
public class Element_Users {
    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Element_Users(String user_name, String login, String password) {

        this.user_name = user_name;
        this.login = login;
        this.password = password;
    }

    private String user_name;
    private String login;
    private String password;
}
