public class BigNumber {
    int[] number;

    public BigNumber(int[] number) {
        this.number=new int[number.length];
        System.arraycopy(number,0,this.number,0,number.length);
    }

    public BigNumber(String num){
        number=new int[num.length()];
        for(int i=0;i<num.length();i++){
            number[i]=Integer.parseInt(num.charAt(i)+"");
        }
    }

    public static int[] potenciaDiez(int potencia,BigNumber n){
        int[] res=new int[n.number.length+potencia];
        for(int i=0;i<n.number.length;i++) {
            res[i]=n.number[i];
        }
        for(int j=0;j<potencia;j++){
            res[n.number.length+j]=0;
        }

        return res;
    }

    public static int[] suma(BigNumber a,BigNumber b){
        int min=a.number.length,max=b.number.length;
        int resum=0;
        boolean carry=false;

        int[] na=a.number;
        int[] nb=b.number;
        if(b.number.length<min){
            min=b.number.length;
            max=a.number.length;
        }
        if(a.number.length==min){
            na=new int[max];
            int dif=max-min;
            for (int i=na.length-1;i>=dif;i--){
                na[i]=a.number[i-dif];
            }
            for (int j=dif-1;j>=0;j--){
                na[j]=0;
            }
        }
        if(b.number.length==min){
            nb=new int[max];
            int dif=max-min;
            for (int i=nb.length-1;i>=dif;i--){
                nb[i]=b.number[i-dif];
            }
            for (int j=dif-1;j>=0;j--){
                nb[j]=0;
            }
        }

        int[] res=new int[max];

        for(int i=max-1;i>=0;i--){
            if(carry){
                resum=na[i]+nb[i]+1;
            }else {
                resum = na[i] + nb[i];
            }
            carry=resum>9;
            res[i]=Math.abs(resum%10);

        }
        if(carry){
            int[] resn=new int[max+1];
            resn[0]=1;
            for(int i=1;i<resn.length;i++){
                resn[i]=res[i-1];
            }
            return resn;
        }

        return res;
    }

    public static int[] substract(BigNumber a,BigNumber b){
        int min=a.number.length,max=b.number.length;
        int resum=0;
        int[] na=a.number;
        int[] nb=b.number;
        if(b.number.length<min){
            min=b.number.length;
            max=a.number.length;
        }
        if(a.number.length==min){
            na=new int[max];
            int dif=max-min;
            for (int i=na.length-1;i>=dif;i--){
                na[i]=a.number[i-dif];
            }
            for (int j=dif-1;j>=0;j--){
                na[j]=0;
            }
        }
        if(b.number.length==min){
            nb=new int[max];
            int dif=max-min;
            for (int i=nb.length-1;i>=dif;i--){
                nb[i]=b.number[i-dif];
            }
            for (int j=dif-1;j>=0;j--){
                nb[j]=0;
            }
        }

        int[] res=new int[max];

        for (int i = max - 1; i >= 0; i--){
            resum = na[i] - nb[i];

            if(resum<0){
                res[i] += 10+resum;
                if(i!=0){
                    res[i-1]--;
                }
            }else {
                res[i]+=resum;
            }
            /*
            if (i != 0) {
                if (resum < 0) {
                    resum = 10 - resum;
                }
            }
            */
            //res[i] = Math.abs(resum % 10);
        }

        return res;
    }

    public static BigNumber multiply(BigNumber a, BigNumber b){
        int n=a.number.length;
        if(b.number.length>n){
            n=b.number.length;
        }
        return multiply(a,b,n);
    }

    private static BigNumber multiply(BigNumber a, BigNumber b, int n){
        if (a.number.length==1&&b.number.length==1) {
            int mult = a.number[0] * b.number[0];
            return new BigNumber(mult + "");
        }else {
            if(a.number.length<b.number.length){
                int dif=b.number.length-a.number.length;
                int[] na=new int[b.number.length];
                System.arraycopy(a.number,0,na,dif,a.number.length);
                a.number=new int[na.length];
                System.arraycopy(na,0,a.number,0,na.length);
            }
            if(b.number.length<a.number.length){
                int dif=a.number.length-b.number.length;
                int[] nb=new int[a.number.length];
                System.arraycopy(b.number,0,nb,dif,b.number.length);
                b.number=new int[nb.length];
                System.arraycopy(nb,0,b.number,0,nb.length);
            }
            int[] al = new int[a.number.length / 2];
            for(int i=0;i<al.length;i++){
                al[i]=a.number[i];
            }
            int[] ar = new int[a.number.length - a.number.length / 2];
            for(int i=0;i<ar.length;i++){
                ar[i]=a.number[i+a.number.length/2];
            }
            int[] bl = new int[b.number.length / 2];
            for(int i=0;i<bl.length;i++){
                bl[i]=b.number[i];
            }
            int[] br = new int[b.number.length - b.number.length / 2];
            for(int i=0;i<br.length;i++){
                br[i]=b.number[i+b.number.length/2];
            }
            int m=a.number.length;
            int[] x1 = multiply(new BigNumber(al), new BigNumber(bl),m/2).number;
            int[] x2 = multiply(new BigNumber(ar), new BigNumber(br),m/2).number;
            int[] alar=suma(new BigNumber(al),new BigNumber(ar));
            int[] blbr=suma(new BigNumber(bl),new BigNumber(br));
            BigNumber x3f=multiply(new BigNumber(alar),new BigNumber(blbr),m/2);
            int[] x3 = x3f.number;
            int[] x3n = substract(new BigNumber(x3), new BigNumber(x2));
            int[] x3nn = substract(new BigNumber(x3n), new BigNumber(x1));
            BigNumber x1p=new BigNumber(potenciaDiez(m,new BigNumber(x1)));
            BigNumber x3p=new BigNumber(potenciaDiez(m/2,new BigNumber(x3nn)));
            BigNumber sum13=new BigNumber(suma(x1p,x3p));
            BigNumber sum132 = new BigNumber(suma(sum13, new BigNumber(x2)));
            return sum132;
        }
    }

    public static void main(String[] args) {
        BigNumber b=new BigNumber("12345");
        BigNumber c=new BigNumber("12301");
        int[] res=multiply(b,c).number;
        for (int i:res){
            System.out.print(i+" ");
        }
        //System.out.print("hola");
    }
}

