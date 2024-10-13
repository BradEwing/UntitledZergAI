package info.opponent.hypothesis;

import java.util.HashSet;

public class Hypothesis {

    // Maybe a percentage with weights?
    public boolean matched = false;

    public HashSet<Hypothesis> children = new HashSet<>();


}
