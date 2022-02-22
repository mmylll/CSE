public class PartA {

public static int hammingDistance(int x, int y) {
    int hamming = x ^ y;
    int sum = 0;
    while (hamming!=0){
        sum += hamming & 1;
        hamming = hamming>>1;
    }
    return sum;
}

    public static void main(String[] args){
        System.out.println(hammingDistance(1,3));
    }
}
