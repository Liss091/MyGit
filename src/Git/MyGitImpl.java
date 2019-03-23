package Git;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class MyGitImpl implements MyGit{

    protected String currentBranch;
    protected ArrayList<String> branches = new ArrayList<>();
    protected Path originDir;
    protected static final String REP_PATH = "C:/java-enterprise/repository/";

    //receives path to a folder to create a repository for it
    public void init(String origin) throws IOException {
        File rep = new File(REP_PATH+"master/");
        if (rep.exists()) {
            Path dirRep = Paths.get(REP_PATH);
            Files.walkFileTree(dirRep, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }
                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        rep.mkdirs();
        FileWriter fw1 = new FileWriter(REP_PATH + "path.txt");
        fw1.write(origin);
        fw1.close();
        setOriginDir();
        System.out.println("Repository for " + originDir + " created");
        writeCurrentBranchToFile("master");
        branches.add("master");
        writeBrancesToFile();

    }

    //receives path to a file to be committed
    public void commit(String filePath) throws IOException {
        getCurrentBranch();
        setOriginDir();
        String dest =  filePath.replace(originDir.toString(),REP_PATH+currentBranch);
        File fileDest = new File(dest);
        fileDest.mkdirs();
        Files.copy(Paths.get(filePath), fileDest.toPath(), REPLACE_EXISTING);
        System.out.println("Commit success!");
    }

    public void branch(String b) throws IOException {
        getBranches();
        if (branches.contains(b)) {
            System.out.println("Branch " + b + " already exists!");
        } else {
            new File(REP_PATH+b).mkdirs();
            branches.add(b);
            writeBrancesToFile();
        }
    }

    public void checkout(String b) throws IOException {
        getBranches();
        if (branches.contains(b)) {
            writeCurrentBranchToFile(b);
        } else {
            System.out.println("Branch doesn`t exist!");
        }
    }

    public void compare() throws IOException {
        setOriginDir();
        Files.walkFileTree(originDir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                System.out.println(file);
                List<String> listOrigin = Files.readAllLines(file);
                String fileRep =  file.toString().replace(originDir.toString(),REP_PATH+"master/");
                if (!new File(fileRep).exists()) {
                    System.out.println("File is not commited to the repository!");
                    return FileVisitResult.CONTINUE;
                }
                List<String> listRep = Files.readAllLines(Paths.get(fileRep));
                compareLines(listOrigin, listRep);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private void compareLines(List<String> listOrigin, List<String> listRep) {
        Iterator i1 = listOrigin.iterator();
        Iterator i2 = listRep.iterator();
        int lineNum = 0;
        while (i1.hasNext() || i2.hasNext()) {
            lineNum++;
            if (!i1.hasNext()) {
                System.out.println(lineNum + "-");
                i2.next();
                continue;
            }
            if (!i2.hasNext()) {
                System.out.println(lineNum + "+");
                i1.next();
                continue;
            }
            String line1 = i1.next().toString();
            String line2 = i2.next().toString();
            if (!line1.equals(line2)) {
                System.out.println(lineNum + "- " + lineNum + "+");
            }
        }
    }

    private void writeBrancesToFile() throws IOException {
        FileWriter fw2 = new FileWriter(REP_PATH + "branches.txt");
        for (String b : branches) {
            fw2.write(b + "\n");
        }
        fw2.close();
    }

    public void getBranches() throws IOException {
        FileReader fr = new FileReader(REP_PATH + "branches.txt");
        BufferedReader br = new BufferedReader(fr);
        String line;
        while ((line = br.readLine()) != null) {
            branches.add(line);
        }
        br.close();
        fr.close();
    }

    public String getCurrentBranch() throws IOException {
        FileReader fr = new FileReader(REP_PATH + "current_branch.txt");
        BufferedReader br = new BufferedReader(fr);
        currentBranch = br.readLine();
        return currentBranch;
    }

    public void writeCurrentBranchToFile(String currentBranch) throws IOException {
        FileWriter fw3 = new FileWriter(REP_PATH + "current_branch.txt");
        fw3.write(currentBranch + "\n");
        fw3.close();
        this.currentBranch = currentBranch;

    }

    public void setOriginDir() throws IOException {
        BufferedReader bf = Files.newBufferedReader(Paths.get(REP_PATH + "path.txt"));
        this.originDir = Paths.get(bf.readLine());
    }
}
