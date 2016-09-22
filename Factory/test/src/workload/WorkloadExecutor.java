package workload;

import au.edu.swin.ict.road.composite.Composite;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * TODO documentation
 */
public class WorkloadExecutor {

    private TraceWorkLoad traceWorkLoad;
    private List<SampleStat> sampleStats = new ArrayList<SampleStat>();
    private List<TenantExecutor> tenantExecutors = new ArrayList<TenantExecutor>();
    private Composite composite;

    public WorkloadExecutor(TraceWorkLoad traceWorkLoad, Composite composite) {
        this.traceWorkLoad = traceWorkLoad;
        this.composite = composite;
    }

    public void execute() {
        for (Sample sampleV : traceWorkLoad.getSamples()) {
            SampleStat sampleStat = new SampleStat(sampleV.getSampleId());
            sampleStats.add(sampleStat);
            long startTime = System.currentTimeMillis();
            for (TenantUnit unit : sampleV.getTenantUnits()) {
                int users = unit.getUsers();
                TenantStat tenantStat = new TenantStat(unit.getTenantName(), users, sampleStat);
                sampleStat.addTenantStat(tenantStat);
                TenantExecutor tenantExecutor = new TenantExecutor(users,
                        users, Long.MAX_VALUE, TimeUnit.NANOSECONDS,
                        new LinkedBlockingDeque<Runnable>());
                tenantExecutors.add(tenantExecutor);
                for (int i = 0; i < users; i++) {
                    tenantExecutor.execute(new UserCall(unit.getTenantName(), tenantStat, composite));
                }
            }
            sampleStat.waitTillComplete();
            long endTime = System.currentTimeMillis();
            sampleStat.setTimeSpent(endTime - startTime);
            for (TenantExecutor executor : tenantExecutors) {
                executor.shutdownNow();
            }
            tenantExecutors.clear();
            long timeDiff = 60 * 1000 - sampleStat.getTimeSpent();
            if (timeDiff > 0) {
                try {
                    Thread.sleep(timeDiff + 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }

        File fout = new File("test/resources/all.txt");
        try {
            FileOutputStream fos = new FileOutputStream(fout);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

            for (SampleStat sampleStat : sampleStats) {
                bw.write(String.valueOf(sampleStat.getSampleId()));
                bw.write("\t");
                bw.write("Time Spent: " + String.valueOf(sampleStat.getTimeSpent()));
                bw.write("\t");
                for (TenantStat unit : sampleStat.getTenantStats()) {
                    bw.write(String.valueOf(unit.getTenantName()));
                    bw.write("\t");
                    bw.write("Completed : " + String.valueOf(unit.getCompleted()));
                    bw.write("\t");
                    bw.write("Failed : " + String.valueOf(unit.getFailures()));
                    bw.write("\t");
                }
                bw.newLine();
            }
            bw.close();
        } catch (Exception e) {
        }

        File f2out = new File("test/resources/completed.txt");
        try {
            FileOutputStream fos = new FileOutputStream(f2out);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

            for (SampleStat sampleStat : sampleStats) {
                bw.write(String.valueOf(sampleStat.getSampleId()));
                bw.write("\t");
                for (TenantStat unit : sampleStat.getTenantStats()) {
                    bw.write(String.valueOf(unit.getCompleted()));
                    bw.write("\t");
                }
                bw.newLine();
            }
            bw.close();
        } catch (Exception e) {
        }

        File f3Out = new File("test/resources/failed.txt");
        try {
            FileOutputStream fos = new FileOutputStream(f3Out);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

            for (SampleStat sampleStat : sampleStats) {
                bw.write(String.valueOf(sampleStat.getSampleId()));
                bw.write("\t");
                for (TenantStat unit : sampleStat.getTenantStats()) {
                    bw.write(String.valueOf(unit.getFailures()));
                    bw.write("\t");
                }
                bw.newLine();
            }
            bw.close();
        } catch (Exception e) {
        }
    }
}
