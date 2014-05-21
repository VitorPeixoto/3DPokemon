package pokemon;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.gluPerspective;
import static org.lwjgl.util.glu.GLU.gluLookAt;

public class Main {

    private boolean running = false;
    //private Walls[] walls; // array for walls
    //private Floor floor;    
    private Character character;
    
    private Environment environment;
    private Terrain floor;
    
    private final float FOV = 60; // field of view
    private final int WIDTH = 1366;
    private final int HEIGHT = 700;
    private final float ASPECT = (float) WIDTH / HEIGHT;

    private Vector3f pos = new Vector3f(0, 0, 0); // position vector
    private Vector3f posfix = new Vector3f(0, 0, 0);
    private float rotY = 0; // rotation along Y axis
    private float characterRotationX = 0; // rotation along Y axis
    private float characterRotationY = 0; // rotation along X axis
    private float rotY2 = 0; // rotation along Y axis
    private float rotX = 0; // rotation along X axis
    private float view = 0; // for character view transformations (along z axis)
    private float posYheight = 0.0f; // Y position of character
    private float z; // variable to move in z axis
    private float x; // variable to move in x axis

// variables for jumping
    private int height = 0;
    private int maxHeight = 30;
    private int countjump = 0;
    private boolean dontjump = false;
    private int jumptimes = 2;

    // fog parameters
    private float fogNear = 15f;
    private float fogFar = 30f;
    private Color fogColor = new Color(0f, 0f, 0f, 1f);

    private String walltex = "res/wall.png";
    private String walltex2 = "res/wall2.png";
    //private LevelGenerator level; // object level will create random cubes in
    private int displaylist;
    
    // scene
    private int countL = 525; // number of cubes (and apparently also height)
    boolean collide; // if collide then stop gravity

    private long lastFrame; // time at last frame

    private int fps; // fps

    private long lastFPS; // last fps

// lightning properties and objects
    private float mat_ambient[] = {1.0f, 1.0f, 1.0f, 1.0f};
    private float light_position[] = {0.0f, -3.0f, 0.0f, 0.0f};
    private float lmodel_ambient[] = {0.7f, 0.7f, 0.7f, 0.7f};
    private float mat_amb_diff[] = {0.1f, 0.5f, 0.8f, 1.0f};

// buffers for lightning
    private FloatBuffer buf;
    private FloatBuffer buf3;
    private FloatBuffer buf4;
    private FloatBuffer buf5;

    public Main() {
        // set Display
        try {
            Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
            Display.setTitle("twolwjgl");
            Display.create();

        } catch (LWJGLException e) {
            System.err.println("LWJGLException! Can't create Display!");
            e.printStackTrace();
            System.exit(0);
        }

    }

    public void init() {
// initialize stuff
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        //gluPerspective(FOV, ASPECT, 0.3f, 50.0f);
        gluPerspective(FOV, ASPECT, 10.3f, 5000.0f);
        glViewport(0, 0, 800, 600);

// view behind character
        gluLookAt(0.0f, 2.0f, 10.0f, 0.0f, 0, 0, 0.0f, 1.0f, 0.0f);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glViewport(0, 0, WIDTH, HEIGHT);
        /*glEnable(GL_FOG);

// buffer for colors
        FloatBuffer fogColours = BufferUtils.createFloatBuffer(4);
        fogColours.put(new float[]{fogColor.r, fogColor.g, fogColor.b,
            fogColor.a});
        glClearColor(fogColor.r, fogColor.g, fogColor.b, fogColor.a);
        fogColours.flip();

// other fog parameters
        glFog(GL_FOG_COLOR, fogColours);
        glFogi(GL_FOG_MODE, GL_LINEAR);
        glHint(GL_FOG_HINT, GL_NICEST);
        glFogf(GL_FOG_START, fogNear);
        glFogf(GL_FOG_END, fogFar);
        glFogf(GL_FOG_DENSITY, 0.0005f);

        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);*/

// buffers for lightning
        buf = BufferUtils.createFloatBuffer(mat_ambient.length);
        buf.put(mat_ambient, 0, mat_ambient.length);
        buf.flip();

        buf3 = BufferUtils.createFloatBuffer(light_position.length);
        buf3.put(light_position, 0, light_position.length);
        buf3.flip();

        buf4 = BufferUtils.createFloatBuffer(lmodel_ambient.length);
        buf4.put(lmodel_ambient, 0, lmodel_ambient.length);
        buf4.flip();

        buf5 = BufferUtils.createFloatBuffer(mat_amb_diff.length);
        buf5.put(mat_amb_diff, 0, mat_amb_diff.length);
        buf5.flip();

// parameters for lightning
        glShadeModel(GL_SMOOTH);
        glMaterial(GL_FRONT, GL_AMBIENT, buf);
        glLight(GL_LIGHT0, GL_POSITION, buf3);
        glLightModel(GL_LIGHT_MODEL_AMBIENT, buf4);
        glEnable(GL_LIGHTING);
        glEnable(GL_LIGHT0);
        glEnable(GL_DEPTH_TEST);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);

        running = true;

        float l = 20.0f; // helper parametr for Walls

// Box vertices
        Vector3f c = new Vector3f(l, -3, -l);
        Vector3f d = new Vector3f(-l, -3, -l);
        Vector3f g = new Vector3f(l, -3, l);
        Vector3f h = new Vector3f(-l, -3, l);

// create floor
        floor = new Terrain();
        floor.createTerrain();
        
        
        environment = new Environment();
        environment.createEnvironment();
        //int k = 0;

// create walls
        /*walls = new Walls[15];
        for (int i = 0; i < walls.length; i++) {

            Vector3f a2 = new Vector3f(-l, 32 + k, -l);
            Vector3f b2 = new Vector3f(l, 32 + k, -l);
            Vector3f c2 = new Vector3f(l, -3 + k, -l);
            Vector3f d2 = new Vector3f(-l, -3 + k, -l);

            Vector3f e2 = new Vector3f(-l, 32 + k, l);
            Vector3f f2 = new Vector3f(l, 32 + k, l);
            Vector3f g2 = new Vector3f(l, -3 + k, l);
            Vector3f h2 = new Vector3f(-l, -3 + k, l);

            k += 35;
            if (i % 2 == 0) {
                walls[i] = new Walls(a2, b2, c2, d2, e2, f2, g2, h2, walltex);
            } else {
                walls[i] = new Walls(a2, b2, c2, d2, e2, f2, g2, h2, walltex2);

            }

        }*/

// create character
        character = new Character();
        character.createCharacter();

// generate level
        //level = new LevelGenerator(countL); // create and generate level

        getDelta();
        lastFPS = getTime();

    }

    public int getDelta() {
// method to get delta value
        long time = getTime();
        int delta = (int) (time - lastFrame);
lastFrame = time;
        return delta;
    }

    public long getTime() {
// actual time
        return (Sys.getTime() * 1000) / Sys.getTimerResolution();
    }

    public void updateFPS() {
// fps counter
        if (getTime() - lastFPS > 1000) {
            Display.setTitle("FPS: " + fps);
            fps = 0;
            lastFPS += 1000;
        }
        fps++;

    }

    public void start() {
// start method

        while (running) {
// game loop
            glEnable(GL_SCISSOR_TEST);

            int delta = getDelta();
            readinput(delta); // read keys
            checkCollisions(); // check for collisions
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear screen
// // screen
// buffer
            glEnable(GL_DEPTH_TEST);
            glEnable(GL_CULL_FACE);
            glCullFace(GL_BACK);

            glColor3f(1, 0.8f, 0.8f);

// render floor
            floor.build();
            
// render walls
            displaylist = glGenLists(1);
            glNewList(displaylist, GL_COMPILE);
            environment.build();
            /*for (int i = 0; i < walls.length; i++) {
                walls[i].build();
            }*/
            glEndList();
            glCallList(displaylist);

            glDisable(GL_CULL_FACE);

// render level
            //level.generate();

            glLoadIdentity();

// character translation and rotation
            glTranslatef(0, 0, view);           
            glRotatef(characterRotationX, 0, 1.0f, 0);
            glRotatef(characterRotationY, 1.0f, 0.0f, 0);
            

// render character
            glLight(GL_LIGHT0, GL_POSITION, buf3);
            character.build();

            posfix.x = -pos.x; // fixed value of pos (x axis)
            posfix.z = -pos.z; // fixed value of pos (z axis)

// translate for y character axis
            glTranslatef(0, -posYheight, 0);
            glLineWidth(1.0f);

// world translation and rotation
            glRotatef(0, 0, 0, 1.0f);
            glRotatef(rotY, 0, 1.0f, 0);
            glRotatef(rotX, 1.0f, 0.0f, 0);
            glTranslatef(pos.x, 0, pos.z);

            //posfix.y = posYheight;

// closing on "X"
            if (Display.isCloseRequested()) {
                stop();
            }
// System.out.println(delta);

            Display.update();
            Display.sync(60);

        }
        //floor.destroy();
        character.destroy();
        //level.destroy();
        glDeleteLists(displaylist, 1);
        Display.destroy();

    }

    public void stop() {
// stop method
        running = false;
    }

    public void readinput(int delta) {
// reading input(keyboard, mouse) method

// rotation parameter max 360 degree
        if (rotY == 360 | rotY == -360) {
            rotY = 0;
        }

        if (rotY2 == 360 | rotY2 == -360) {
            rotY2 = 0;
        }

        if (rotX == 360 | rotX == -360) {
            rotX = 0;
        }

        if (!Keyboard.isKeyDown(Keyboard.KEY_LEFT)
                && !Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
// rotate back(Y axis) when view keys not pressed
            //rotY2 = 0;
        }

        if (!Keyboard.isKeyDown(Keyboard.KEY_UP)
                && !Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
// rotate back(X axis) when view keys not pressed
            //rotX = 0;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_A)
                && !Keyboard.isKeyDown(Keyboard.KEY_S)
                && !Keyboard.isKeyDown(Keyboard.KEY_D)
                && Keyboard.isKeyDown(Keyboard.KEY_W)
                && !Keyboard.isKeyDown(Keyboard.KEY_E)
                && !Keyboard.isKeyDown(Keyboard.KEY_Q)) {
// moving code for key A and W
            x = (float) (0.1 * Math.sin(Math.toRadians(45 - rotY)) * delta * 0.1f);
            z = (float) (0.1 * Math.cos(Math.toRadians(45 - rotY)) * delta * 0.1f);

pos.x += x;
            pos.z += z;

        }

        if (!Keyboard.isKeyDown(Keyboard.KEY_A)
                && !Keyboard.isKeyDown(Keyboard.KEY_S)
                && Keyboard.isKeyDown(Keyboard.KEY_D)
                && Keyboard.isKeyDown(Keyboard.KEY_W)
                && !Keyboard.isKeyDown(Keyboard.KEY_E)
                && !Keyboard.isKeyDown(Keyboard.KEY_Q)) {
// moving code for key D and W
            x = (float) -(0.1 * Math.cos(Math.toRadians(45 - rotY)) * delta * 0.1f);
            z = (float) (0.1 * Math.sin(Math.toRadians(45 - rotY)) * delta * 0.1f);

            pos.x += x;
            pos.z += z;

        }

        if (Keyboard.isKeyDown(Keyboard.KEY_A)
                && !Keyboard.isKeyDown(Keyboard.KEY_S)
                && !Keyboard.isKeyDown(Keyboard.KEY_D)
                && !Keyboard.isKeyDown(Keyboard.KEY_W)
                && !Keyboard.isKeyDown(Keyboard.KEY_E)
                && !Keyboard.isKeyDown(Keyboard.KEY_Q)) {
// moving code for key A
            x = (float) -(0.1 * Math.cos(Math.toRadians(180 - rotY)) * delta * 0.1f);
            z = (float) (0.1 * Math.sin(Math.toRadians(180 - rotY)) * delta * 0.1f);

            pos.x += x;
            pos.z += z;

        }

        if (Keyboard.isKeyDown(Keyboard.KEY_S)
                && !Keyboard.isKeyDown(Keyboard.KEY_A)
                && !Keyboard.isKeyDown(Keyboard.KEY_D)
                && !Keyboard.isKeyDown(Keyboard.KEY_W)
                && !Keyboard.isKeyDown(Keyboard.KEY_E)
                && !Keyboard.isKeyDown(Keyboard.KEY_Q)) {
// moving code for key S

            z = (float) -(0.1 * Math.sin(Math.toRadians(90 - rotY)) * delta * 0.1f);
            x = (float) (0.1 * Math.cos(Math.toRadians(90 - rotY)) * delta * 0.1f);

            pos.z += z;
            pos.x += x;

        }

        if (Keyboard.isKeyDown(Keyboard.KEY_W)
                && !Keyboard.isKeyDown(Keyboard.KEY_A)
                && !Keyboard.isKeyDown(Keyboard.KEY_S)
                && !Keyboard.isKeyDown(Keyboard.KEY_D)
                && !Keyboard.isKeyDown(Keyboard.KEY_E)
                && !Keyboard.isKeyDown(Keyboard.KEY_Q)) {
// moving code for key W

            z = (float) (0.1 * Math.sin(Math.toRadians(90 - rotY)) * delta * 0.1f);
            x = (float) -(0.1 * Math.cos(Math.toRadians(90 - rotY)) * delta * 0.1f);

pos.x += x;
            pos.z += z;

        }

        if (Keyboard.isKeyDown(Keyboard.KEY_D)
                && !Keyboard.isKeyDown(Keyboard.KEY_A)
                && !Keyboard.isKeyDown(Keyboard.KEY_S)
                && !Keyboard.isKeyDown(Keyboard.KEY_W)
                && !Keyboard.isKeyDown(Keyboard.KEY_E)
                && !Keyboard.isKeyDown(Keyboard.KEY_Q)) {
// moving code for key D

            x = (float) (0.1 * Math.cos(Math.toRadians(180 - rotY)) * delta * 0.1f);
            z = (float) -(0.1 * Math.sin(Math.toRadians(180 - rotY)) * delta * 0.1f);

            pos.x += x;
            pos.z += z;

        }

        if (Keyboard.isKeyDown(Keyboard.KEY_E)) {
        // clockwise rotation for key E
            rotY += 3.0f;                
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_Q)) {
        // counter clockwise rotation for key Q

            rotY -= 3.0f;

        }

        if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
        // clockwise rotation for key RIGHT (showing character’s side)
            characterRotationX -= 3.0f;
            rotY += 3.0f;
            //rotY2 += 3.0f;

        }

        if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
// counter clockwise rotation for key LEFT (showing character’s
// side)
            characterRotationX += 3.0f;    
            rotY -= 3.0f;
            //rotY2 -= 3.0f;

        }

        if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
// counter clockwise rotation for key UP
            rotX -= 3.0f;
            characterRotationY += 3.0f;

        }

        if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
// counter clockwise rotation for key DOWN (showing character’s
// side)
            characterRotationY -= 3.0f;
            rotX += 3.0f;

        }
        
        if(Keyboard.isKeyDown(Keyboard.KEY_RETURN)) {
            characterRotationX = 0.0f;
            characterRotationY = 0.0f;
            rotX = 0.0f;
            rotY = 0.0f;
            rotY2 = 0.0f;
            pos.x = 0.0f;
            pos.y = 0.0f;
            pos.z = 0.0f;
            posYheight = 10;
        }

        if (Keyboard.next()) {

            if (Keyboard.isKeyDown(Keyboard.KEY_V)) {
// change player’s view
                if (view == 8.0f) {
                    view = 0;
                } else {
                    view = 8.0f;
                }
            }

            if (Keyboard.isKeyDown(Keyboard.KEY_SPACE) && !dontjump) {
// jump counter
                if (countjump >= jumptimes) {
                    dontjump = true;
                    height = 0;
                } else {

                    if (countjump < jumptimes) {
                        countjump++;
                        height = 0;

                    }
                }

            }

// max height for jump
            if (height >= maxHeight && !Keyboard.isKeyDown(Keyboard.KEY_SPACE)
                    && !dontjump) {
                height = 0;

            }

        }

        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE) && !dontjump) {
// jump

            if (height < maxHeight) {
                if (!dontjump) {
                    posYheight += 0.7f;
                }
            }
            height++;

        }

        if (Keyboard.isKeyDown(Keyboard.KEY_Z)) {
// floating up
            posYheight += (0.9);

        }
        
        if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            //character.
        }

        updateFPS();
    }

    public void checkCollisions() {
// check for collisions (wall and box)

// Wall checkers
        /*if ((int) pos.x == 19) {
            pos.x = 19;
        }

        if ((int) pos.x == -19) {
            pos.x = -19;
        }

        if ((int) pos.z == 19) {
            pos.z = 19;
        }

        if ((int) pos.z == -19) {
            pos.z = -19;
        }*/

// collision checks for level cubes, also collision response
        /*for (int i = 0; i < level.boxNumber; i++) {
            if (posYheight >= level.box[i].getC().y + 1
                    && posYheight <= level.box[i].getA().y + 3) {
                if (posfix.x >= level.box[i].getA().x
                        & posfix.x <= level.box[i].getB().x) {

                    if (posfix.z > level.box[i].getA().z
                            & posfix.z < level.box[i].getE().z) {

                        if ((int) posYheight >= level.box[i].getC().y + 1) {
                            // posYheight -= 0.5f ;
                            collide = true;

                        } else {
                            collide = false;
                        }

                        if ((int) posYheight <= level.box[i].getA().y + 4) {
                            posYheight += 0.25f;
                            collide = true;

                            dontjump = false;
                            countjump = 0;
                            height = 0;
                        } else {
                            collide = false;
                        }

                        if (posYheight < level.box[i].getC().y + 0.9f) {
                            collide = true;
                            posYheight -= 0.25f;
                        }
                        collide = false;

                        if ((int) posfix.x >= level.box[i].getA().x
                                && posYheight <= level.box[i].getA().y + 2
                                && posYheight >= level.box[i].getC().y) {
                            pos.x -= x;

                        } else {

                            if ((int) posfix.x <= level.box[i].getB().x
                                    && posYheight <= level.box[i].getA().y + 2
                                    && posYheight >= level.box[i].getC().y) {
                                pos.x -= x;
                            }
                        }

                        if ((int) posfix.z >= level.box[i].getA().z
                                && posYheight <= level.box[i].getA().y + 2
                                && posYheight >= level.box[i].getC().y) {
                            pos.z -= z;
                        } else {

                            if ((int) posfix.z <= level.box[i].getE().z
                                    && posYheight <= level.box[i].getA().y + 2
                                    && posYheight >= level.box[i].getC().y) {
                                pos.z -= z;
                            }
                        }

                    }
                }
            } else {
                collide = false;
            }
        }*/

// don’t fall when touching floor, and clear height counter
        if (posYheight <= 10.0f) {
            dontjump = false;
            countjump = 0;
            height = 0;
            posYheight = 10.0f;
            if (posYheight <= 0.0f && Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
                posYheight = 0.7f;
                posYheight -= 0.5f;

            }

        }
// if player not collide posYheight will drop = gravity
        if (!collide) {
            posYheight -= 0.5f;
        }

    }

    public static void main(String[] args) {
        Main m = new Main();
        m.init();
        m.start();

    }

}
