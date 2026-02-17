package model;

public record Permission(String name, String resource, String description) {
    public Permission{
        if(name == null || name.isBlank()) throw new IllegalArgumentException("Error: name required");
        if(resource == null || resource.isBlank()) throw new IllegalArgumentException("Error: resource required");
        if(description == null || description.isBlank()) throw new IllegalArgumentException("Error: description required");

        name = name.toUpperCase().replace(" ", "_");
        resource = resource.toLowerCase();
    }

    public String format(){
        return String.format("%s on %s: %s", name, resource, description);
    }

    public boolean matches(String namePattern, String resourcePattern){
        return name.contains(namePattern) && resource.contains(resourcePattern);
    }
}
