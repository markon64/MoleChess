package org.chernovia.molechess;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.chernovia.lib.zugserv.Connection;

public class MoleUser {
    String oauth;
    String name;
    private Connection conn;
    private MoleData data;

    public class MoleData {
        String about;
        int wins, losses, rating;

        public MoleData(int wins, int losses, int rating, String about) {
            this.wins = wins;
            this.losses = losses;
            this.rating = rating;
            this.about = about;
        }

        public String toString() {
            return "Wins: " + wins + ", Losses: " + losses + ", Rating: " + rating;
        }

        public JsonNode toJSON(boolean ratingOnly) {
            ObjectNode node = MoleServ.OBJ_MAPPER.createObjectNode();
            if (!ratingOnly) {
                node.put("wins", wins);
                node.put("losses", losses);
                node.put("about", about);
            }
            node.put("rating", rating);
            return node;
        }
    }

    public MoleUser(Connection c, String o, String n) {
        this.conn = c;
        if (conn != null) conn.setStatus(Connection.Status.STATUS_OK);
        this.oauth = o;
        this.name = n;
    }

    public MoleData setData(int wins, int losses, int rating, String about) {
        data = new MoleData(wins, losses, rating, about);
        return data;
    }

    public MoleData getData() {
        return data;
    }

    public boolean sameConnection(Connection c) {
        return (this.conn == c);
    }

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection c) {
        conn = c;
    }

    public boolean isActiveUser() {
        if (conn == null) return false;
        Connection.Status status = conn.getStatus();
        if (status == null) return false;
        else return (status.equals(Connection.Status.STATUS_OK));
    }

    public void tell(String msg) {
        tell("serv_msg", msg);
    }

    public void tell(String type, String msg) {
        ObjectNode node = MoleServ.OBJ_MAPPER.createObjectNode();
        node.put("msg", msg);
        tell(type, (JsonNode) node);
    }

    public void tell(String type, JsonNode node) {
        if (this.conn != null)
            this.conn.tell(type, node);
    }

    public JsonNode toJSON(boolean ratingOnly) {
        ObjectNode obj = MoleServ.OBJ_MAPPER.createObjectNode();
        obj.put("name", this.name);
        if (data != null) obj.set("data", data.toJSON(ratingOnly));
        return (JsonNode) obj;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof MoleUser)) return false;
        return ((MoleUser) o).oauth.equals(this.oauth);
    }
}
