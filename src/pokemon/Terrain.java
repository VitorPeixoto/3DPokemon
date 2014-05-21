package pokemon;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;



public class Terrain {
    private Texture texture;
    private final String textureLocation = "textures/TerrainTexture.png";
    
    private int displayListCh;
    private int displayListChar;
    
    private ObjectLoader objModel;
    //private final String modelLocation = "models/Terrain.obj";
    private final String modelLocation = "/home/Peixoto/NetBeansProjects/Pokemon/src/models/Terrain.obj";
    public Terrain() {    
        loadTexture();
    }

    public void build() {
        texture.bind();
        glCallList(displayListChar); // display list
    }

    public void destroy() {
        glDeleteLists(displayListChar, 1); // removing display list
    }

    public void createTerrain() {
        displayListChar = glGenLists(1);
        glNewList(displayListChar, GL_COMPILE);

        Model m = null;
        try {            
            m = ObjectLoader.load(new File(modelLocation));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Display.destroy();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            Display.destroy();
            System.exit(1);
        }

        glBegin(GL_TRIANGLES);

        for (Face face : m.faces) {
            if(face.hasTextures) {
                Vector2f t1 = m.textures.get((int) face.textures.x - 1);
                glTexCoord2f(t1.x, 1 - t1.y);
            }            
            
            if(face.hasNormals) {
                Vector3f n1 = m.normals.get((int) face.normal.x - 1);
                glNormal3f(n1.x, n1.y, n1.z);
            }    
            Vector3f v1 = m.vertices.get((int) face.vertex.x - 1);
            glVertex3f(v1.x, v1.y, v1.z);
            if(face.hasTextures) {            
                Vector2f t2 = m.textures.get((int) face.textures.y - 1);
                glTexCoord2f(t2.x, 1 - t2.y);
            }    
            
            if(face.hasNormals) {            
                Vector3f n2 = m.normals.get((int) face.normal.y - 1);
                glNormal3f(n2.x, n2.y, n2.z);
            }    
            Vector3f v2 = m.vertices.get((int) face.vertex.y - 1);
            glVertex3f(v2.x, v2.y, v2.z);
            if(face.hasTextures) {            
                Vector2f t3 = m.textures.get((int) face.textures.z - 1);
                glTexCoord2f(t3.x, 1 - t3.y);
            }    
            
            if(face.hasNormals) {            
                Vector3f n3 = m.normals.get((int) face.normal.z - 1);
                glNormal3f(n3.x, n3.y, n3.z);
            }    
            Vector3f v3 = m.vertices.get((int) face.vertex.z - 1);
            glVertex3f(v3.x, v3.y, v3.z);

        }
        glEnd();

        glEndList();        
    }

    public void loadTexture() {
        try {            
            texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(textureLocation));
            TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(textureLocation));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
