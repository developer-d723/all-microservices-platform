
package org.example.commondto.dto.event;

import lombok.Generated;

public class UserEvent {
    private EventType eventType;
    private String email;

    @Generated
    public EventType getEventType() {
        return this.eventType;
    }

    @Generated
    public String getEmail() {
        return this.email;
    }

    @Generated
    public void setEventType(final EventType eventType) {
        this.eventType = eventType;
    }

    @Generated
    public void setEmail(final String email) {
        this.email = email;
    }

    @Generated
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof UserEvent)) {
            return false;
        } else {
            UserEvent other = (UserEvent)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$eventType = this.getEventType();
                Object other$eventType = other.getEventType();
                if (this$eventType == null) {
                    if (other$eventType != null) {
                        return false;
                    }
                } else if (!this$eventType.equals(other$eventType)) {
                    return false;
                }

                Object this$email = this.getEmail();
                Object other$email = other.getEmail();
                if (this$email == null) {
                    if (other$email != null) {
                        return false;
                    }
                } else if (!this$email.equals(other$email)) {
                    return false;
                }

                return true;
            }
        }
    }

    @Generated
    protected boolean canEqual(final Object other) {
        return other instanceof UserEvent;
    }

    @Generated
    public int hashCode() {
        int result = 1;
        Object $eventType = this.getEventType();
        result = result * 59 + ($eventType == null ? 43 : $eventType.hashCode());
        Object $email = this.getEmail();
        result = result * 59 + ($email == null ? 43 : $email.hashCode());
        return result;
    }

    @Generated
    public String toString() {
        String var10000 = String.valueOf(this.getEventType());
        return "UserEvent(eventType=" + var10000 + ", email=" + this.getEmail() + ")";
    }

    @Generated
    public UserEvent() {
    }

    @Generated
    public UserEvent(final EventType eventType, final String email) {
        this.eventType = eventType;
        this.email = email;
    }
}