package itlab.teleport;

import org.json.simple.JSONObject;

/**
 * Created by alex on 30.07.15.
 */
public class Element_list {


    public Element_list(String teg, String uri, String id, String login) {
        this.teg = teg;
        this.uri = uri;
        this.id = id;
        this.login = login;
    }

    private String teg;
    private String uri;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    private String login;

    public String getTeg() {
        return teg;
    }

    public void setTeg(String teg) {
        this.teg = teg;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public JSONObject getJOSON()
    {
        JSONObject json=new JSONObject();

        json.put("TAG", teg);
        json.put("LOGIN", login);
        json.put("ID", id);
        json.put("URI", uri);
        return json;

    }
}
