import java.util.Date;

public class Entity {
    protected String name;
    protected static int counter = 0;
    protected int entityID;

    //todo change entity id
    public Entity() {
        this.name = "";
        //counter++;
        //this.entityID = counter;
    }


    public boolean equals(Entity otherEntity) {
        return entityID == otherEntity.entityID;
    }


    public Entity(String name, int id) {
        this.name = name;
        this.entityID = id;
        counter++;
        //this.entityID = counter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return "Name: " + this.name + " Entity ID: " + this.entityID;
    }
    public String toHTML() {
        return "<b>" + this.name + "</b><i> " + this.entityID + "</i>";
    }
    public String toXML() {
        return "<entity><name>" + this.name + "</name><ID> " + this.entityID + "</ID></entity>";
    }
}