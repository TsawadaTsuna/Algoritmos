public class Prueba {
    public static void main(String[] args) {
        int[] a=new int[]{1,2,3,4,5};
        int[] b=new int[10];
        System.arraycopy(a,0,b,5,a.length);
        for (int c:b){
            System.out.print(c+" ");
        }
    }
}
