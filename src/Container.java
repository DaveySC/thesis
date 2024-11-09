public class Container {
    private String graph;
    private int geo;
    private int indep;
    private int dep;


    public Container(String graph, int geo, int indep, int dep) {
        this.graph = graph;
        this.geo = geo;
        this.indep = indep;
        this.dep = dep;
    }

    public int getDep() {
        return dep;
    }

    public int getGeo() {
        return geo;
    }

    public String getGraph() {
        return graph;
    }

    public int getIndep() {
        return indep;
    }
}
