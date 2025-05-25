package id.ac.ui.cs.advprog.manajemen_iklan.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IklanStatusTest {

    @Test
    void testEnumValues() {
        // Test that all expected enum values exist
        IklanStatus[] expectedValues = {IklanStatus.ACTIVE, IklanStatus.INACTIVE, IklanStatus.EXPIRED};
        IklanStatus[] actualValues = IklanStatus.values();

        assertEquals(expectedValues.length, actualValues.length);
        
        for (IklanStatus expected : expectedValues) {
            boolean found = false;
            for (IklanStatus actual : actualValues) {
                if (expected == actual) {
                    found = true;
                    break;
                }
            }
            assertTrue(found, "Expected enum value " + expected + " not found");
        }
    }

    @Test
    void testValueOf() {
        // Test valueOf method for each enum constant
        assertEquals(IklanStatus.ACTIVE, IklanStatus.valueOf("ACTIVE"));
        assertEquals(IklanStatus.INACTIVE, IklanStatus.valueOf("INACTIVE"));
        assertEquals(IklanStatus.EXPIRED, IklanStatus.valueOf("EXPIRED"));
    }

    @Test
    void testValueOf_InvalidValue() {
        // Test that valueOf throws exception for invalid values
        assertThrows(IllegalArgumentException.class, () -> IklanStatus.valueOf("INVALID"));
        assertThrows(IllegalArgumentException.class, () -> IklanStatus.valueOf("active"));
        assertThrows(IllegalArgumentException.class, () -> IklanStatus.valueOf(""));
        assertThrows(NullPointerException.class, () -> IklanStatus.valueOf(null));
    }

    @Test
    void testEnumToString() {
        // Test that toString returns the correct string representation
        assertEquals("ACTIVE", IklanStatus.ACTIVE.toString());
        assertEquals("INACTIVE", IklanStatus.INACTIVE.toString());
        assertEquals("EXPIRED", IklanStatus.EXPIRED.toString());
    }

    @Test
    void testEnumName() {
        // Test that name() returns the correct name
        assertEquals("ACTIVE", IklanStatus.ACTIVE.name());
        assertEquals("INACTIVE", IklanStatus.INACTIVE.name());
        assertEquals("EXPIRED", IklanStatus.EXPIRED.name());
    }

    @Test
    void testEnumOrdinal() {
        // Test ordinal values (order of declaration)
        assertEquals(0, IklanStatus.ACTIVE.ordinal());
        assertEquals(1, IklanStatus.INACTIVE.ordinal());
        assertEquals(2, IklanStatus.EXPIRED.ordinal());
    }

    @Test
    void testEnumComparison() {
        // Test that enum comparison works correctly
        assertTrue(IklanStatus.ACTIVE.compareTo(IklanStatus.INACTIVE) < 0);
        assertTrue(IklanStatus.INACTIVE.compareTo(IklanStatus.EXPIRED) < 0);
        assertTrue(IklanStatus.EXPIRED.compareTo(IklanStatus.ACTIVE) > 0);
        assertEquals(0, IklanStatus.ACTIVE.compareTo(IklanStatus.ACTIVE));
    }

    @Test
    void testEnumEquality() {
        // Test equality
        assertEquals(IklanStatus.ACTIVE, IklanStatus.ACTIVE);
        assertNotEquals(IklanStatus.ACTIVE, IklanStatus.INACTIVE);
        assertNotEquals(IklanStatus.ACTIVE, null);
        assertNotEquals(IklanStatus.ACTIVE, "ACTIVE");
    }

    @Test
    void testEnumInSwitch() {
        // Test that enums work correctly in switch statements
        for (IklanStatus status : IklanStatus.values()) {
            String result = switch (status) {
                case ACTIVE -> "Advertisement is active";
                case INACTIVE -> "Advertisement is inactive";
                case EXPIRED -> "Advertisement has expired";
            };
            
            assertNotNull(result);
            assertFalse(result.isEmpty());
        }
    }

    @Test
    void testEnumWithCollections() {
        // Test that enums work with collections
        java.util.Set<IklanStatus> statusSet = java.util.EnumSet.allOf(IklanStatus.class);
        assertEquals(3, statusSet.size());
        assertTrue(statusSet.contains(IklanStatus.ACTIVE));
        assertTrue(statusSet.contains(IklanStatus.INACTIVE));
        assertTrue(statusSet.contains(IklanStatus.EXPIRED));
    }
}