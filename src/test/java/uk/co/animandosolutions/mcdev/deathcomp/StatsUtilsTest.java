package uk.co.animandosolutions.mcdev.deathcomp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import uk.co.animandosolutions.mcdev.deathcomp.utils.StatsUtils;

public class StatsUtilsTest {

    @Test
    void testComputeMedian_EmptyList() {
        var testData = Arrays.asList(new Double[] {});
        var actual = StatsUtils.computeMedian(testData);
        
        assertEquals(Double.NaN, actual);
    }

    @Test
    void testComputeMedian() {
        var testData = Arrays.asList(1d, 2d, 3d, 4d, 5d);
        var actual = StatsUtils.computeMedian(testData);
     
        assertEquals(3, actual);
    }
    
    @Test
    void testComputeMedian2() {
        var testData = Arrays.asList(2d, 2d, 3d, 4d, 100d);
        var actual = StatsUtils.computeMedian(testData);
        
        assertEquals(3, actual);
    }
    
    @Test
    void testComputeMedian3() {
        var testData = Arrays.asList(1d, 2d, 3d, 4d, 5d, 100d);
        var actual = StatsUtils.computeMedian(testData);
        
        assertEquals(3.5, actual);
    }

}
