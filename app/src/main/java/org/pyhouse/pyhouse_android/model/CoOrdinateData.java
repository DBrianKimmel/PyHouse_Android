package org.pyhouse.pyhouse_android.model;

/**
 * Created by briank on 5/28/17.
 */

public class CoOrdinateData {
    public float x;
    public float y;
    public float z;

    /**
     * Convert (1.2, 3.4, 5.6) into a CoOrdinate
     *
     * @param x is X xoordinate
     * @param y is Y coordinate
     * @param z is Z coordinate
     */
    public CoOrdinateData( float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * convert a string into x, y and z floats
     *
     * @param xyz is [1.2,3.4, 5.6]
     */
    public CoOrdinateData(String xyz) {
        String[] ary = xyz.replace("[", "").replace("]", "").split(",");
        this.x = Float.parseFloat(ary[0]);
        this.y = Float.parseFloat(ary[1]);
        this.z = Float.parseFloat(ary[2]);
    }

    public float getX() {
        return x;
    }
    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }
    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }
    public void setZ(float z) {
        this.z = z;
    }

    /**
     * Convert a CoOrdinate to a string representation "[1.2,3.4,5.6]"
     *
     * @param xyz
     * @return
     */
    public String toString(CoOrdinateData xyz){
        String ret = '[' + Float.toString(xyz.getX()) + ',' +
                Float.toString(xyz.getY()) + ',' +
                Float.toString(xyz.getZ()) + ']';
        return ret;
    }
}
