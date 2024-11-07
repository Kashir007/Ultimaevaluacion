package Telefonos;

public class EmergencyContact {
    private String id;  // Cambiar a String en lugar de int
    private String name;
    private String phoneNumber;

    // Constructor actualizado para usar String en lugar de int
    public EmergencyContact(String id, String name, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getId() {  // Devuelve String en lugar de int
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}


