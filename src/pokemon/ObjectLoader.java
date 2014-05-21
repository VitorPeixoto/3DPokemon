package pokemon;

    import java.io.BufferedReader;
    import java.io.File;
    import java.io.FileNotFoundException;
    import java.io.FileReader;
    import java.io.IOException;

    import org.lwjgl.util.vector.Vector2f;
    import org.lwjgl.util.vector.Vector3f;

    public class ObjectLoader {

    public static Model load(File file) throws FileNotFoundException,
    IOException {
    Model m = new Model();

    BufferedReader reader = new BufferedReader(new FileReader(file));
    // read coords from obj file
    String line;
    while ((line = reader.readLine()) != null) {
    if (line.startsWith("v ")) {
    float x = Float.valueOf(line.split(" ")[1]);
    float y = Float.valueOf(line.split(" ")[2]);
    float z = Float.valueOf(line.split(" ")[3]);
    m.vertices.add(new Vector3f(x, y, z));
    } else if (line.startsWith("vn ")) {
    float x = Float.valueOf(line.split(" ")[1]);
    float y = Float.valueOf(line.split(" ")[2]);
    float z = Float.valueOf(line.split(" ")[3]);
    m.normals.add(new Vector3f(x, y, z));
    } else if (line.startsWith("vt ")) {
    float x = Float.valueOf(line.split(" ")[1]);
    float y = Float.valueOf(line.split(" ")[2]);
    m.textures.add(new Vector2f(x, y));
    } else if (line.startsWith("f ")) {
        try {
            Vector3f vertexIndices = new Vector3f(Float.valueOf(line
            .split(" ")[1].split("/")[0]), Float.valueOf(line
            .split(" ")[2].split("/")[0]), Float.valueOf(line
            .split(" ")[3].split("/")[0]));
        
            Vector3f textureIndices = new Vector3f(Float.valueOf(line
            .split(" ")[1].split("/")[1]), Float.valueOf(line
            .split(" ")[2].split("/")[1]), Float.valueOf(line
            .split(" ")[3].split("/")[1]));

            Vector3f normalIndices = new Vector3f(Float.valueOf(line
            .split(" ")[1].split("/")[2]), Float.valueOf(line
            .split(" ")[2].split("/")[2]), Float.valueOf(line
            .split(" ")[3].split("/")[2]));
            
            m.faces.add(new Face(vertexIndices, textureIndices, normalIndices));
        }   catch(NumberFormatException vertexNnormals) {
            Vector3f vertexIndices = new Vector3f(Float.valueOf(line
            .split(" ")[1].split("/")[0]), Float.valueOf(line
            .split(" ")[2].split("/")[0]), Float.valueOf(line
            .split(" ")[3].split("/")[0]));
        
            Vector3f normalIndices = new Vector3f(Float.valueOf(line
            .split(" ")[1].split("/")[2]), Float.valueOf(line
            .split(" ")[2].split("/")[2]), Float.valueOf(line
            .split(" ")[3].split("/")[2]));
            
            m.faces.add(new Face(vertexIndices, normalIndices));
        }   catch (ArrayIndexOutOfBoundsException vertexOnly) {
            try {                
                Vector3f vertexIndices = new Vector3f(Float.valueOf(line
                .split(" ")[1]), Float.valueOf(line
                .split(" ")[2]), Float.valueOf(line
                .split(" ")[3]));
            
                m.faces.add(new Face(vertexIndices));
            } catch(NumberFormatException vertexNtexture) {
                Vector3f vertexIndices = new Vector3f(Float.valueOf(line
                .split(" ")[1].split("/")[0]), Float.valueOf(line
                .split(" ")[2].split("/")[0]), Float.valueOf(line
                .split(" ")[3].split("/")[0]));
        
                Vector3f textureIndices = new Vector3f(Float.valueOf(line
                .split(" ")[1].split("/")[1]), Float.valueOf(line
                .split(" ")[2].split("/")[1]), Float.valueOf(line
                .split(" ")[3].split("/")[1]));
                
                m.faces.add(new Face(vertexIndices, textureIndices, true));
            }    
        }
    }
    }
    reader.close();
    return m;
    }

    }
