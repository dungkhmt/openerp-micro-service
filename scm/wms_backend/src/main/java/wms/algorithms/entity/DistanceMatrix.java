package wms.algorithms.entity;

import com.google.gson.internal.LinkedTreeMap;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class DistanceMatrix {
    Object hints;
    Object info;
    ArrayList<Path> paths;
}
