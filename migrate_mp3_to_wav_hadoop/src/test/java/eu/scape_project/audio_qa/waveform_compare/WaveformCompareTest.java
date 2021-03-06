package eu.scape_project.audio_qa.waveform_compare;

import eu.scape_project.audio_qa.AudioQASettings;
import eu.scape_project.audio_qa.ffmpeg_migrate.FfmpegMigrate;
import eu.scape_project.audio_qa.ffmpeg_migrate.FfmpegMigrationMapper;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.apache.hadoop.mrunit.mapreduce.MapReduceDriver;
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * eu.scape_project.audio_qa.waveform_compare
 * User: baj@statsbiblioteket.dk
 * Date: 2014-01-20
 */
public class WaveformCompareTest {
    MapReduceDriver<LongWritable, Text, LongWritable, Text, LongWritable, Text> mapReduceDriver;
    MapDriver<LongWritable, Text, LongWritable, Text> mapDriver;
    ReduceDriver<LongWritable, Text, LongWritable, Text> reduceDriver;

    Text inputText = new Text("output/MigrateMp3ToWav/freestylemix_-_hisboyelroy_-_Revolve/" +
            "freestylemix_-_hisboyelroy_-_Revolve.mp3_ffmpeg.wav    output/MigrateMp3ToWav/" +
            "freestylemix_-_hisboyelroy_-_Revolve/freestylemix_-_hisboyelroy_-_Revolve.mp3_mpg321.wav");
    Text outputText = new Text("Success 1\n" +
            "Failure 0");
            //new Text("output/MigrateMp3ToWav/" + AudioQASettings.DEFAULT_JOBID +
            //AudioQASettings.SLASH + "freestylemix_-_hisboyelroy_-_Revolve" +
            //AudioQASettings.UNDERSCORE + "compare" + AudioQASettings.DOTLOG);

    @Before
    public void setUp() {
        AudioQASettings.MAPPER_OUTPUT_DIR = "output/MigrateMp3ToWav/";
        WaveformCompareMapper mapper = new WaveformCompareMapper();
        WaveformCompare.WaveformCompareReducer reducer = new WaveformCompare.WaveformCompareReducer();
        mapDriver = new MapDriver<LongWritable, Text, LongWritable, Text>();
        mapDriver.setMapper(mapper);
        reduceDriver = new ReduceDriver<LongWritable, Text, LongWritable, Text>();
        reduceDriver.setReducer(reducer);
        mapReduceDriver = new MapReduceDriver<LongWritable, Text, LongWritable, Text, LongWritable, Text>();
        mapReduceDriver.setMapper(mapper);
        mapReduceDriver.setReducer(reducer);
    }

    @Test
    public void testMapper() throws IOException {
        mapDriver.withInput(new LongWritable(0), inputText);
        Text mapperOutputText = new Text("Success\n" +
                "Offset: -1152\n" +
                "Similarity: 0.999863\n");
        mapDriver.withOutput(new LongWritable(0), mapperOutputText);
        mapDriver.runTest();
    }

    @Test
    public void testReducer() throws IOException {
        List<Text> values = new ArrayList<Text>();
        values.add(new Text("Success\n" +
                "Offset: -1152\n" +
                "Similarity: 0.999863\n"));
        //TODO test multiple values
        reduceDriver.withInput(new LongWritable(0), values);
        reduceDriver.withOutput(new LongWritable(0), outputText);
        reduceDriver.runTest();
    }

    @Test
    public void testMapReduce() throws IOException {
        mapReduceDriver.withInput(new LongWritable(0), inputText);
        mapReduceDriver.addOutput(new LongWritable(0), outputText);
        mapReduceDriver.runTest();
    }
}
