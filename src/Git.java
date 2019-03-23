import Git.MyGitImpl;

import java.io.IOException;

public class Git {

    public static void main(String[] args) {
        MyGitImpl git = new MyGitImpl();
        try {
            if (args.length != 0)
                switch (args[0]) {
                    case "init" :
                        git.init(args[1]);
                        break;
                    case "commit" :
                        git.commit(args[1]);
                        break;
                    case "branch" :
                        git.branch(args[1]);
                        break;
                    case "checkout" :
                        git.checkout(args[1]);
                        break;
                    case "compare" :
                        git.compare();
                        break;
                    default:
                        System.out.println("Available commands: init, commit, branch, checout, compare");
                }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
