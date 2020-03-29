package net.smackem.jobotwar.runtime;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

public abstract class EngineObject {
    final static GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();

    abstract Geometry geometry();
}
