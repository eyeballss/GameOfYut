import java.util.*;

/**
 * Basic Yut 11/20
 */
public class Yut {

    private String[] yut = {"DOE", "GAE", "GIRL", "YUT", "MOE"};
    private final int raceRoom = 20;    // 실제 윷판 20칸으로 구성되어있는데 16칸으로 초기 구성하여 일단 고정합니다.
    // 추후 최종판은 29판 대각선 없는건 20판으로 바꿔야됩니다.
    private final int numOfDevice = 2;
    private ArrayList<Device> device =new ArrayList<Device>(numOfDevice * 2);// device;
    private String[] board; // 윷판은 1차원 배열로 되어잇어 이곳에 말들의 위치를 넣고 출력합니다.
    private final String blank = "_";
    private final String user = "U";
    private final String com = "C";
    // done을 start method 밖으로 꺼낸 이유는
    // selectComputerHorse method에서 말을 먹거나 엎을시 한번더 윷을 던져야 되기 때문입니다.
    private boolean done; // 윷던지는 반복문 체크입니다. false일시 반복하지 않고 true인 경우 계속해서 반복합니다.

    // Initialize Yut Matrix
    public Yut() {

        // 윷판 출력 시 배열 0의 위치는 말들이 시작하지 않은 위치라서 사용하지 않습니다.
        board = new String[raceRoom+1];

        // type과 type에 따른 index(몇번 말인지)를 정해 device객체를 생성
        int userIndex = 0;
        int comIndex = 0;

        // 각각의 말들은 ArrayList의 유저 / 컴퓨터 순으로 들어있으며
        // 각각의 유저와 컴퓨터의 말들의 각각의 갯수만큼 번호를 가지고 0번부터 시작하고 있습니다.
        for (int i = 0 ; i < numOfDevice*2; i++) {
            if (i < numOfDevice)
                device.add(new Device(user, userIndex++));
            else
                device.add(new Device(com, comIndex++));
        }

        done = false;
    }

    // Game start
    public void start() {

        int userPreIndex=0, comPreIndex=0, index;
        Scanner in = new Scanner(System.in);
        clean();
        System.out.println("Play Game of Yut !!");

        // 윷판을 _ 으로 초기화합니다.
        // 게임이 끝나고 다시 시작하기 위해서 start method 에서 초기화합니다.
        for(int i=1; i<=16; i++)
            board[i] = blank;

        //윷을 던지기 위해 0 이상의 숫자를 선택하고 넣으세요!
        System.out.println("pick num>0 and put to throw Yut for you!");

        /*
            윷을 던진 후 턴 진행 여부에 대해서 코드가 너무 변경되어서 일단 제외시켰습니다.
            후에 턴에 관해서 변경된 소스 코드에서 구현해주시면 감사하겠습니다.
            사용자 차례와 컴퓨터 차례가 번갈아 가면서 진행되고 있습니다.
         */
        while(in.nextInt()!=0){
            // 0 입력시 강제종료 , 0이 아닐때는 게임이 끝날때까지 게임을 진행합니다.

            // 윷을 던지고 난 결과값 입니다.
            int result;

            System.out.println("\nPlayer Phase");
            do {
                // 플레이어의 차례입니다.ㄴ
                done = false; // 반복 후 done을 초기화합니다.
                // 윷을 던진 결과를 저장합니다.
                result = throwYut();
                // 플레이어에겐 어떤 말을 움직일지 묻고 원하는 말을 움직이게 합니다.
                do {
                    System.out.print("Select Horse (0 ~ " + (numOfDevice - 1) + ") : ");
                    index = in.nextInt();   // 말번호를 입력 받습니다.
                    // 사용금지된 말의 경우 말을 움직이지 못하게 합니다.
                    if (device.get(index).getType().equals(blank))
                        System.out.println("This horse can't select");
                } while(device.get(index).getType().equals(blank)); // 금지된 말이 아닐때까지 반복합니다.

                // 선택된 말을 ArrayList에서 꺼냅니다.
                // 유저의 말들은 0 ~ 말의 갯수만큼의 번호까지 ArrayList에 입력되어있습니다.
                Device d = device.get(index);

                // 해당 말의 윷을 던져 나온 결과를 입력합니다. 윷이나 모일시 계속해서 반복합니다.
                done = d.input(result);

                // 말을 먹거나 엎은 경우 말들의 위치를 변경시키고 반복을 하여야 하여 체크합니다.
                // 사용자인지 컴퓨터인지 확인하도록 파라미터를 넣습니다. //먹거나 업은 경우 윷 한번 더 던진다고 하자.
                done = checkHorese(user);

                // 말을 움직인 후 게임에서 이길지 체크 하여 이긴다면 게임을 끝냅니다.
                if (d.getLocate() > raceRoom) {
                    System.out.println("user win!\n");
                    break;
                }

                // 유저가 전의 이동했던 말의 위치를 초기화합니다.
                board[userPreIndex] = "_";
                // 말의 전의 위치에 새로 이동한 위치를 넣습니다.
                userPreIndex = d.getLocate();

                // 윷판을 출력합니다.
                showBoard();
            } while(done);

            System.out.println("\nComputer Phase");
            do {
                // 컴퓨터의 차례입니다.
                done = false; // 반복 후 done을 초기화합니다.
                // 윷을 던진 결과 값을 저장합니다.
                result = throwYut();

                // 컴퓨터는 selectComputerHorse method를 통해 움직일 말을 결정합니다.
                index = selectComputerHorse(result);

                // 선택된 말을 ArrayList에서 꺼냅니다.
                // 컴퓨터의 말들은 유저 말의 갯수만큼을 더해주어야 합니다.
                Device d = device.get(index + numOfDevice);

                // 해당 말의 윷을 던져 나온 결과를 입력합니다. 윷이나 모일시 계속해서 반복합니다.
                done = d.input(result);
                // 말을 먹거나 엎은 경우 말들의 위치를 변경시키고 반복을 하여야 하여 체크합니다.
                // 사용자인지 컴퓨터인지 확인하도록 파라미터를 넣습니다.
                done = checkHorese(com);

                // 말을 움직인 후 게임에서 이길지 체크 후 이긴다면 게임을 끝냅니다.
                if (d.getLocate() > raceRoom) {
                    System.out.println("com win!\n");
                    break;
                }

                // 컴퓨터가 전의 이동했던 말의 위치를 초기화합니다.
                board[comPreIndex] = "_";
                // 말이 전의 위치에 새로 이동한 말의 위치를 넣습니다.
                comPreIndex = d.getLocate();

                // 윷판을 출력합니다.
                showBoard();
            } while(done);
            //윷을 던지기 위해 0 이상의 숫자를 선택하고 넣으세요!
            System.out.println("pick num>0 and put to throw Yut for you!");
        }

    }


    // reversePrint method와 함께 콘솔에 출력하는 방법을 구현하였고
    // 변경시 문제가 발생 할 수 있으니 가급적 수정 하지 않는 방향으로 개발 부탁드립니다.
    // 설명하자면 유저/컴퓨터의 턴을 입력받고 보드에 입력된 말들의 위치를
    // 콘솔창에 윷놀이판 모양으로 출력한다고 보시면 됩니다.
    private void showBoard() {
        int u1=device.get(0).getLocate(), u2=device.get(1).getLocate();
        int c1=device.get(2).getLocate(), c2=device.get(3).getLocate();
        int temp;
        String output="S";
        for(int i=0; i<=raceRoom; i++){
            output+="[";
            temp=(i==0)?(4):(2);
            if(u1==i) { output+="u1"; temp--;}
            if(u2==i) { output+="u2"; temp--;}
            if(c1==i) { output+="c1"; temp--;}
            if(c2==i) { output+="c2"; temp--;}

            for(;temp>0;temp--) output+="  ";
            output+="]";

            if(i==0) output+=" ";
        }
        System.out.println(output);
        /*
        // 보드판에 플레이어와 컴퓨터의 말의 위치를 입력하는 방법입니다.
        for(int i = 1; i <= raceRoom; i++) {
            for(int j=0; j < device.size(); j++) {
                Device d = device.get(j);
                // 보드 위치에 말이 있으면 보드판에 말을 넣습니다.
                if(i == d.getLocate())
                    board[i] = d.getType();
            }
        }

        // 보드에 입력받은 말의 위치를 콘솔창에 출력하는 방법입니다.
        for(int i=0; i<3 ; i++){
            if(i==0){
                reversePrint(10);
                System.out.println();
            }
            else if(i==1){
                int n=11;
                for(int j=0; j!=6; j+=2) {
                    System.out.println(board[n] + "  \t" + board[n - 7-j]);
                    n++;
                }
            }
            else{
                for(int j=15; j<20; j++) System.out.print(board[j]);
                System.out.println(board[0]);
            }
        }
        */
    }

    // 보드 출력시 필요한 메소드 이므로 수정지 문제가 발생 할 수 있습니다.
    // 다만 다른 개발자 분들이 이해 할 수 있도록 주석을 달아 주시면 감사하겠습니다.
    private void reversePrint(int n){
        if(n<=4) return;
        System.out.print(board[n]);
        reversePrint(n - 1);
    }

    // Throw yut
    public int throwYut() {
        Random r = new Random();
        System.out.println("Throws Yut!");
        int result = r.nextInt(5);
        System.out.println("Resulit is " + yut[result]);
        return result;
    }

    // 컴퓨터가 말을 선택하여 말의 번호를 돌려줍니다.
    public int selectComputerHorse(int move) {

        int locateNum = 0;
        // 컴퓨터의 말들만 체크합니다.
        for(int i=0+numOfDevice; i<device.size(); i++) {
            Device d = device.get(i);
            // 출발하지 않은 말이 있는지 체크
            locateNum += d.getLocate();
        }

        // 말이 하나도 앞으로 안나간 경우
        if(locateNum == 0) {
            // 첫번째 말을 움직입니다. (인덱스 번호 0)
            return 0;
        }
        else {
            // 1순위 상대말을 먹을 수 있을때
            for(int i=0; i<numOfDevice; i++) {
                Device d = device.get(i);
                for(int j=0+numOfDevice; i<device.size(); i++) {
                    Device comd = device.get(j);
                    // 사용자의 말의 위치랑 컴퓨터의 말이 앞으로 움직일 위치가 같을 때
                    if(d.getLocate() == comd.getLocate() + move) {
                        // 컴퓨터의 말의 번호를 반환합니다.
                        // 금지말이 아니면 반환합니다.
                        if(!d.getType().equals(blank))
                            return comd.getLocate();
                    }
                }
            }
            // 2순위 말을 엎을 수 있을때
            for(int i=0+numOfDevice; i<device.size(); i++) {
                Device d = device.get(i);
                for(int j=0+numOfDevice; j<device.size(); j++) {
                    Device d2 = device.get(j);
                    // 컴퓨터의 말의 위치가 컴퓨터의 말이 앞으로 움직일 위치와 같을 떄
                    if (d.getLocate() == d2.getLocate() + move) {
                        // 컴퓨터의 말의 번호를 반환합니다.
                        // 금지말이 아니면 반환합니다.
                        if(!d.getType().equals(blank))
                            return d2.getNumber();
                    }
                }
            }
            // 아무것도 아닐때 : 가장 앞선말을 움직입니다.
            int fast = 0;
            // 가장 앞에 있는 말을 찾습니다
            Device fastd = null;
            for(int i=0+numOfDevice; i<device.size(); i++){
                Device d = device.get(i);
                if(d.getLocate() > fast) {
                    fastd = d;
                }
            }
            // 컴퓨터의 말의 번호를 반환합니다.
            // 금지말이 아니면 반환합니다.
            if(!fastd.getType().equals(blank))
                return fastd.getNumber();
        }
        // 답이없을 경우 첫번째 말을 움직입니다.
        return 0;
    }

    // 말을 먹거나 엎었을 경우를 처리하고 반복해야할지를 정해줍니다.
    public boolean checkHorese(String horse) {

        for(int i=0; i<device.size(); i++) {
            Device d = device.get(i);
            for (int j = 0; j < device.size(); j++) {
                Device d2 = device.get(j);
                // 말이 시작위치에 있는게 아니고 같은 말이 아니면서
                // 같은 위치의 말이 있다면 먹거나 엎은 것이므로 반복을 하도록 true를 반환 합니다.
                if (d.getNumber() != d2.getNumber()
                        && d.getLocate()!=0 && d2.getLocate()!=0
                        && d.getLocate() == d2.getLocate()) {
                    // 컴퓨터 턴인 경우 (컴퓨터가 먹거나 엎은 경우)
                    if (horse.equals(com)) {
                        // 컴퓨터가 먹은 경우
                        // i or j 가 사용자인 경우 말을 먹은 것이므로 사용자의 말의 위치를 0으로 바꿉니다.
                        if (d.getType().equals(user) || d2.getType().equals(user)) {
                            if (d.getType().equals(user)) {
                                d.setLocate(0);
                            }
                            else if (d2.getType().equals(user))
                                d.setLocate(0);
                        }
                        // 컴퓨터가 엎은 경우
                        // i j가 둘다 컴퓨터인 경우 말을 엎은 경우 이므로
                        if (d.getType().equals(com) && d.getType().equals(com)) {
                            // 앞의 번호의 말의 타입을 바꿔버리고 뒤의 번호의 말을 못쓰게하여 하나의 말로 합쳤습니다.
                            if (d.getNumber() < d2.getNumber()) {
                                d.setType(com + com);
                                d2.setType(blank);
                                d2.setLocate(0);
                            }
                            else {
                                d2.setType(com + com);
                                d.setType(blank);
                                d.setLocate(0);
                            }
                        }
                    }
                    // 사용자 턴인 경우 (사용자가 먹거나 엎은 경우)
                    else if (horse.equals(user)) {
                        // 사용자가 먹은 경우
                        // i or j 가 컴퓨터인 경우 말을 먹은 것이므로 컴퓨터의 말의 위치를 0으로 바꿉니다.
                        if (d.getType().equals(com) || d2.getType().equals(com)) {
                            if (d.getType().equals(com)) {
                                d.setLocate(0);
                            }
                            else if (d2.getType().equals(com))
                                d.setLocate(0);
                        }
                        // 사용자가 엎은 경우
                        // i j가 둘다 사용자인 경우 말을 엎은 경우 이므로
                        if (d.getType().equals(user) && d.getType().equals(user)) {
                            // 앞의 번호의 말의 타입을 바꿔버리고 뒤의 번호의 말을 못쓰게하여 하나의 말로 합쳤습니다.
                            if (d.getNumber() < d2.getNumber()) {
                                System.out.println(d.getNumber() + " " + d2.getNumber());
                                d.setType(user + user);
                                d2.setType(blank);
                                d2.setLocate(0);
                            }
                            else {
                                System.out.println(d2.getNumber() + " " + d.getNumber());
                                d2.setType(user + user);
                                d.setType(blank);
                                d.setLocate(0);
                            }
                        }
                    }
                    // 먹거나 엎은 경우 한번 더 반복하도록 true를 반환합니다.
                    return true;
                }
            }
        }
        return false;
    }

    // Clean Console 일단 이런식으로 쓰는거라서 이렇게 해봄 반복 횟수는 출력보고 결정
    public void clean() {
        for (int i=0; i<30; i++)
            System.out.println();
    }
}
