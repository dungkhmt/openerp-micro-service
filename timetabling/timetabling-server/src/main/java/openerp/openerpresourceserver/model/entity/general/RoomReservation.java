package openerp.openerpresourceserver.model.entity.general;

public class RoomReservation {
    private int startTime;
    private int weekday;
    private String room;

    public RoomReservation(int startTime, int weekday, String room) {
        this.startTime = startTime;
        this.weekday = weekday;
        this.room = room;
    }
    
}
