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



public class Environment {
    private Texture texture[];
    private final String textureLocation[];
    private final int faces = 6;
    
    private int displayListCh;
    private int displayListChar[];
    
    private ObjectLoader objModel;
    //private final String modelLocation = "models/Terrain.obj";
    private final String modelLocation[];
    
    public Environment() {    
        texture = new Texture[faces];
        textureLocation = new String[faces];
        
        for(int i = 0; i < faces; i++) {
            textureLocation[i] = "textures/environment/Environment00"+i+".png";
            //textureLocation[i] = "textures/Texture.png";
        }
        
        modelLocation = new String[faces];
        
        for(int i = 0; i < faces; i++) {
            modelLocation[i] = "/home/Peixoto/NetBeansProjects/Pokemon/src/models/environment/Environment00"+i+".obj";
        }
        
        displayListChar = new int[faces];
        
        loadTexture();
    }

    public void build() {
        for(int i = 0; i < faces; i++) {
            texture[i].bind();
            glCallList(displayListChar[i]); // display list
        }        
    }

    public void destroy() {
        for(int i = 0; i < faces; i++) 
            glDeleteLists(displayListChar[i], 1); // removing display list
    }

    public void createEnvironment() {
        for(int i = 0; i < faces; i++) {
            displayListChar[i] = glGenLists(1);  
            
            glNewList(displayListChar[i], GL_COMPILE);

            Model m[] = new Model[faces];
                try {            
                    m[i] = ObjectLoader.load(new File(modelLocation[i]));
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
                
            for (Face face : m[i].faces) {
                if(face.hasTextures) {
                    Vector2f t1 = m[i].textures.get((int) face.textures.x - 1);
                    glTexCoord2f(t1.x, 1 - t1.y);
                }            
            
                if(face.hasNormals) {
                    Vector3f n1 = m[i].normals.get((int) face.normal.x - 1);
                    glNormal3f(n1.x, n1.y, n1.z);
                }    
                
                Vector3f v1 = m[i].vertices.get((int) face.vertex.x - 1);
                glVertex3f(v1.x, v1.y, v1.z);
                
                if(face.hasTextures) {            
                    Vector2f t2 = m[i].textures.get((int) face.textures.y - 1);
                    glTexCoord2f(t2.x, 1 - t2.y);
                }    
            
                if(face.hasNormals) {            
                    Vector3f n2 = m[i].normals.get((int) face.normal.y - 1);
                    glNormal3f(n2.x, n2.y, n2.z);
                }    
                
                Vector3f v2 = m[i].vertices.get((int) face.vertex.y - 1);
                glVertex3f(v2.x, v2.y, v2.z);
                
                if(face.hasTextures) {            
                    Vector2f t3 = m[i].textures.get((int) face.textures.z - 1);
                    glTexCoord2f(t3.x, 1 - t3.y);
                }    
            
                if(face.hasNormals) {            
                    Vector3f n3 = m[i].normals.get((int) face.normal.z - 1);
                    glNormal3f(n3.x, n3.y, n3.z);
                }    
                
                Vector3f v3 = m[i].vertices.get((int) face.vertex.z - 1);
                glVertex3f(v3.x, v3.y, v3.z);

        }
        glEnd();
        glEndList();
        }        
    }

    public void loadTexture() {
        try {            
            for(int i = 0; i < faces; i++) {
                texture[i] = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(textureLocation[i]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
