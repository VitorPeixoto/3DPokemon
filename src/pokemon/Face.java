package pokemon;

import org.lwjgl.util.vector.Vector3f;

public class Face {
    public Vector3f vertex = new Vector3f();
    public Vector3f normal = new Vector3f();
    public Vector3f textures = new Vector3f();
    public boolean hasNormals = false;
    public boolean hasTextures = false;
    
    public Face(Vector3f vertex, Vector3f textures, Vector3f normal) {
        this.vertex = vertex;
        this.hasNormals = true;
        this.normal = normal;
        this.hasTextures = true;
        this.textures = textures;
    }

    public Face(Vector3f vertex, Vector3f normal) {
        this.vertex = vertex;
        this.hasNormals = true;
        this.normal = normal;
    }
    
    public Face(Vector3f vertex, Vector3f texture, boolean textures) {
        this.vertex = vertex;
        hasTextures = true;
        this.textures = texture;
    }
    
    public Face(Vector3f vertex) {
        this.vertex = vertex;
    }

}
