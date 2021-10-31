import java.util.Scanner;

public class test {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to alphaFileSystem!");
        System.out.println("supported commands:cat,hex,(cat)write,copy,create,(cat)setsize,help,quit");
        while (true){
            System.out.print("alpha-");
            String command = scanner.nextLine();
            String op = command.split(" ")[0];
            String[] commandArray;
            try{
                switch (op){
                    case "cat":
                        commandArray = command.split(" ",3);
                        if(commandArray.length == 3 && isFm(commandArray[1]))
                            alphaUtil.alphaCat(commandArray[1],commandArray[2]);
                        else if (!isFm(commandArray[1]))
                            throw new ErrorCode(ErrorCode.NO_FILE_MANAGER);
                        else
                            throw new ErrorCode(ErrorCode.COMMAND_ERROR);
                        break;
                    case "hex":
                        commandArray = command.split(" ",3);
                        if(commandArray.length == 3  && isBm(commandArray[1]) && isNumber(commandArray[2]))
                            alphaUtil.alphaHex(commandArray[1],Integer.parseInt(commandArray[2]));
                        else if (!isBm(commandArray[1]))
                            throw new ErrorCode(ErrorCode.NO_BLOCK_MANAGER);
                        else
                            throw new ErrorCode(ErrorCode.COMMAND_ERROR);
                        break;
                    case "write":
                        commandArray = command.split(" ",4);
                        if(commandArray.length > 3 && isNumber(commandArray[1])){
                            int temp = commandArray[0].length()+commandArray[1].length()+commandArray[2].length()+3;
                            alphaUtil.alphaWrite(Integer.parseInt(commandArray[1]),commandArray[2],command.substring(temp));
                            System.out.println("Write Success!");
                        }
                        else
                            throw new ErrorCode(ErrorCode.COMMAND_ERROR);
                        break;
                    case "copy":
                        commandArray = command.split(" ",5);
                        if(commandArray.length == 5 && isFm(commandArray[1]) && isFm(commandArray[3])){
                            alphaUtil.alphaCopy(commandArray[1],commandArray[2],commandArray[3],commandArray[4]);
                            System.out.println("Copy Success!");
                        }
                        else if (!isFm(commandArray[1]) || !isFm(commandArray[3]))
                            throw new ErrorCode(ErrorCode.NO_FILE_MANAGER);
                        else
                            throw new ErrorCode(ErrorCode.COMMAND_ERROR);
                        break;
                    case "quit":
                        alphaUtil.finish();
                        System.exit(0);
                        break;
                    case "create":
                        commandArray = command.split(" ",3);
                        if(commandArray.length == 3)
                            alphaUtil.alphaCreate(commandArray[1],commandArray[2]);
                        else
                            throw new ErrorCode(ErrorCode.COMMAND_ERROR);
                        break;
                    case "setsize":
                        commandArray = command.split(" ",2);
                        if(commandArray.length == 2 && isNumber(commandArray[1])) {
                            alphaUtil.alphaSetSize(Integer.parseInt(commandArray[1]));
                            System.out.println("Set size Success!");
                        }
                        else
                            throw new ErrorCode(ErrorCode.COMMAND_ERROR);
                        break;
                    case "help":
                        alphaUtil.alphaHelp();
                        break;
                    default:
                        throw new ErrorCode(ErrorCode.COMMAND_ERROR);
                }
            }catch (ErrorCode errorCode){
                System.out.println(errorCode.getErrorText(errorCode.getErrorCode()));
            }

        }
    }
    private static boolean isNumber(String str){
        String reg = "^[0-9]+(.[0-9]+)?$";
        return str.matches(reg);
    }
    private static boolean isFm(String str){
        return config.myFileManagerMap.containsKey(new StringId(str));
    }
    private static boolean isBm(String str){
        return config.myBlockManagerMap.containsKey(new StringId(str));
    }
}
