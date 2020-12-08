package gr.uth.displayphotosv2;

import java.io.Serializable;

/*A helper class whose objects represent an item of the gallery.
* Each object is defined by its absolute path in the device storage and its type(Photo/Video)*/
public class File implements Serializable {

    private String path;
    private Type type;

    public File(String path, Type type) {
        this.path = path;
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
