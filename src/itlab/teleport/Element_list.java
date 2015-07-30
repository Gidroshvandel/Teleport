package itlab.teleport;

import org.json.simple.JSONObject;

/**
 * Created by alex on 30.07.15.
 */
public class Element_list {
    public Element_list(String teg, String uri) {
        this.teg = teg;
        this.uri = uri;
    }

    public  class Element_Handler {

    }

    private String teg;
    private String uri;

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
        json.put("URI", uri);
        return json;

    }
}
