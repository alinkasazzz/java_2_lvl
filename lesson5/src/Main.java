public class Main {
    static final int size = 1000000;
    static final int h = size / 2;
    static float[] arr = new float[size];


    public static void main(String[] args) {
        for (int i = 0; i < size; i++) {
            arr[i] = 1;
        }
        long a = System.currentTimeMillis();
        method1(arr, 0);
        System.out.println(System.currentTimeMillis() - a);
        method2();

    }

    public static void method1(float[] arr, int start) {
        for (int i = 0; i < arr.length; i++) {
            arr[i] = (float) (arr[i] * Math.sin(0.2f + (start + i) / 5) * Math.cos(0.2f +(start + i)/ 5) * Math.cos(0.4f +(start + i) / 2));
        }
    }

    public static void method2() {
        long a = System.currentTimeMillis();
        float[] a1 = new float[h];
        float[] a2 = new float[h];
        System.arraycopy(arr, 0, a1, 0, h);
        System.arraycopy(arr, h, a2, 0, h);
        Thread t1 = new Thread(() -> {
            method1(a1, 0);
            });
        Thread t2 = new Thread(() -> {
            method1(a2, h);
            });
        t1.start();
        t2.start();

        try {
                t1.join();
                t2.join();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        System.arraycopy(a1, 0, arr, 0, h);
        System.arraycopy(a2, 0, arr, h, h);
        System.out.println(System.currentTimeMillis() - a);
    }
}

