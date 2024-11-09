package es.us.dp1.lx_xy_24_25.your_game_name.tableCard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RotationVectors {

    public static List<Map<String, Integer>> createRotationToVector() {
        List<Map<String, Integer>> rotationToVector = new ArrayList<>();

        // 0: (0, -1)
        Map<String, Integer> vector0 = new HashMap<>();
        vector0.put("x", 0);
        vector0.put("y", -1);
        rotationToVector.add(vector0);

        // 1: (-1, 0)
        Map<String, Integer> vector1 = new HashMap<>();
        vector1.put("x", -1);
        vector1.put("y", 0);
        rotationToVector.add(vector1);

        // 2: (1, 0)
        Map<String, Integer> vector2 = new HashMap<>();
        vector2.put("x", 1);
        vector2.put("y", 0);
        rotationToVector.add(vector2);

        // 3: (0, 1)
        Map<String, Integer> vector3 = new HashMap<>();
        vector3.put("x", 0);
        vector3.put("y", 1);
        rotationToVector.add(vector3);

        return rotationToVector;
    }
}