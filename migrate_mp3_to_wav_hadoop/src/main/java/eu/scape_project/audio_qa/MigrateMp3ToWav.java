package eu.scape_project.audio_qa;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.chain.ChainMapper;
import org.apache.hadoop.mapreduce.lib.chain.ChainReducer;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;

/**
 * Create the map-reduce job for migrating (and characterising) the mp3 files on the given input list.
 * eu.scape_project
 * User: baj@statsbiblioteket.dk
 * Date: 6/28/13
 * Time: 10:22 AM
 */
public class MigrateMp3ToWav extends Configured implements Tool {

    static public class MigrationReducer extends Reducer<LongWritable, Text, LongWritable, Text> {

        @Override
        protected void reduce(LongWritable exitCode, Iterable<Text> output, Context context)
                throws IOException, InterruptedException {
            Text failedFiles = new Text("");
            if (exitCode.equals(0)) {
                for (Text listOfAssociatedFiles : output) {
                    //TODO parse output and update repository with right events and files
                    failedFiles = new Text(failedFiles.toString()+listOfAssociatedFiles+"\n");
                }
            } else {
                for (Text listOfAssociatedFiles : output) {
                    //TODO parse output ...
                    failedFiles = new Text(failedFiles.toString()+listOfAssociatedFiles+"\n");
                }
            }
            context.write(exitCode, failedFiles);
        }
    }

    public int run(String[] args) throws Exception {
        Configuration configuration = getConf();

        Job job = Job.getInstance(configuration);
        job.setJarByClass(MigrateMp3ToWav.class);
        //job.setJobName("chain");

        int n = args.length;
        if (n > 0)
            TextInputFormat.addInputPath(job, new Path(args[0]));
        if (n > 1)
            SequenceFileOutputFormat.setOutputPath(job, new Path(args[1]));
        if (n > 2)
            configuration.set("map.outputdir", args[2]);

        ChainMapper.addMapper(job, MigrationMapper.class,
                LongWritable.class, Text.class, LongWritable.class, Text.class,  configuration);

        ChainMapper.addMapper(job, QAMapper.class,
                LongWritable.class, Text.class, LongWritable.class, Text.class,configuration);

        /*ChainReducer.setReducer(job, MigrationReducer.class,
                LongWritable.class, Text.class, LongWritable.class, Text.class, configuration);*/
        job.setReducerClass(MigrationReducer.class);

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        job.setOutputKeyClass(LongWritable.class);
        job.setOutputValueClass(Text.class);

        job.setNumReduceTasks(1);
        return job.waitForCompletion(true) ? 0 : -1;

        /*
        Configuration configuration = getConf();

        Job job = new Job(configuration, "Migrate Mp3 To Wav");
        job.setJarByClass(MigrateMp3ToWav.class);

        int n = args.length;
        if (n > 0)
            TextInputFormat.addInputPath(job, new Path(args[0]));
        if (n > 1)
            SequenceFileOutputFormat.setOutputPath(job, new Path(args[1]));
        if (n > 2)
            configuration.set("map.outputdir", args[2]);

        job.setMapperClass(MigrationMapper.class);
        job.setCombinerClass(MigrationReducer.class);
        job.setReducerClass(MigrationReducer.class);

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);

        job.setNumReduceTasks(1);
        return job.waitForCompletion(true) ? 0 : -1;
          */
    }

    public static void main(String[] args) throws Exception {
        System.exit(ToolRunner.run(new MigrateMp3ToWav(), args));
    }
}
