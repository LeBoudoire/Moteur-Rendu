
import algebra.*;

/**
 * author: cdehais
 */
public class Transformation  {

    Matrix worldToCamera;
    Matrix projection;
    Matrix calibration;

    public Transformation () {
        try {
            worldToCamera = new Matrix ("W2C", 4, 4);
            projection = new Matrix ("P", 3, 4);
            calibration = Matrix.createIdentity (3);
            calibration.setName ("K");
        } catch (InstantiationException e) {
            /* should not reach */
        }
    }

    public void setLookAt (Vector3 cam, Vector3 lookAt, Vector3 up) {
        try {
        // compute rotation
        	Vector3 z = new Vector3(lookAt.get(0)-cam.get(0),lookAt.get(1)-cam.get(1),lookAt.get(2)-cam.get(2));
        	z.normalize();
        	
        	Vector3 x = up.cross(z);
        	x.normalize();
        	
        	Vector3 y = z.cross(x);
        	
        	Matrix N_t = new Matrix ("N", 3, 3);
        	//e1
        	N_t.set(0,0,x.getX());
        	N_t.set(0,1,x.getY());
        	N_t.set(0,2,x.getZ());
        	
        	//e2
        	N_t.set(1,0,y.getX());
        	N_t.set(1,1,y.getY());
        	N_t.set(1,2,y.getZ());
        	
        	//e3
        	N_t.set(2,0,z.getX());
        	N_t.set(2,1,z.getY());
        	N_t.set(2,2,z.getZ());

        // compute translation
        	//Matrix N_t = N.transpose();
        	Vector t = N_t.multiply(cam);
        	t.scale(-1);

        	//modifier worldToCamera
        	worldToCamera.set(0, 0, N_t.get(0, 0));
        	worldToCamera.set(0, 1, N_t.get(0,1));
        	worldToCamera.set(0, 2, N_t.get(0,2));
        	worldToCamera.set(0, 3, t.get(0));
        	
        	
        	worldToCamera.set(1, 0, N_t.get(1,0));
        	worldToCamera.set(1, 1, N_t.get(1,1));
        	worldToCamera.set(1, 2, N_t.get(1,2));
        	worldToCamera.set(1, 3, t.get(1));
        	
        	worldToCamera.set(2, 0, N_t.get(2,0));
        	worldToCamera.set(2, 1, N_t.get(2,1));
        	worldToCamera.set(2, 2, N_t.get(2,2));
        	worldToCamera.set(2, 3, t.get(2));
        	
        	worldToCamera.set(3, 0, 0.0);
        	worldToCamera.set(3, 1, 0.0);
        	worldToCamera.set(3, 2, 0.0);
        	worldToCamera.set(3, 3, 1.0);
        	
        } catch (Exception e) { /* unreached */ };
        
        System.out.println ("Modelview matrix:\n" + worldToCamera);

    }

    public void setProjection () {
    	projection.set(0, 0, 1.0);
    	projection.set(1, 1, 1.0);
    	projection.set(2, 2, 1.0);

        System.out.println ("Projection matrix:\n" + projection);
    }

    public void setCalibration (double focal, double width, double height) {


	calibration.set(0, 0, focal);
	calibration.set(1, 1, focal);
	calibration.set(2, 2, 1.0);
	calibration.set(0, 2, width/2);
	calibration.set(1, 2, height/2);

	System.out.println ("Calibration matrix:\n" + calibration);
    }

    /**
     * Projects the given homogeneous, 4 dimensional point onto the screen.
     * The resulting Vector as its (x,y) coordinates in pixel, and its z coordinate
     * is the depth of the point in the camera coordinate system.
     */  
    public Vector3 projectPoint (Vector p)
        throws SizeMismatchException, InstantiationException {
	Vector ps = new Vector(3);
	
	Vector pc = worldToCamera.multiply(p);
	
	Vector p_projete = projection.multiply(pc);
	double zc = p_projete.get(2);
	p_projete.scale(1/zc);
	
	ps = calibration.multiply(p_projete);
	ps.set(2, zc);
	
	//System.out.println ("pc:\n" + pc);
	//System.out.println ("ps:\n" + ps);
	//System.out.println ("p_projete:\n" + p_projete);
	
	return new Vector3 (ps);
    }
    
    /**
     * Transform a vector from world to camera coordinates.
     */
    public Vector3 transformVector (Vector3 v)
        throws SizeMismatchException, InstantiationException {
        /* Doing nothing special here because there is no scaling */
        Matrix R = worldToCamera.getSubMatrix (0, 0, 3, 3);
        Vector tv = R.multiply (v);
        return new Vector3 (tv);
    }
    
}

