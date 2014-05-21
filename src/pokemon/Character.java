package pokemon;
import com.sun.javafx.sg.prism.NGPhongMaterial;
import static org.lwjgl.opengl.GL11.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javafx.scene.paint.Material;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Character {
    // Class for creating a Character

    Texture texture;
    //String tex = "res/tex3.png";    
    //String tex = "res/tex2.png";
    //String tex = "res/StickText.png;
    //String tex = "res/leaves.tga.001.png";
    String tex = "textures/ElectrodeTexture.png";
    int displaylistch;
    int displaylistchar;
    ObjectLoader obj;
    static String loc = "models/Electrode.obj";

    public Character() {
        loadTexture();
    }

    public void build() {
        texture.bind();
        glCallList(displaylistchar); // display list
    }

    public void destroy() {
        glDeleteLists(displaylistchar, 1); // removing display list
    }

    public void createCharacter() {

        displaylistchar = glGenLists(1);
        glNewList(displaylistchar, GL_COMPILE);

        Model m = null;
        try {
            m = ObjectLoader.load(new File("/home/Peixoto/NetBeansProjects/Pokemon/src/models/Electrode.obj"));
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
                // System.out.println((int)face.textures.x - 1);
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
        // loader for textures (using slick2d helper)

        try {
            // load texture from PNG file
            texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(tex));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
