public class Main {
    static final int size = 1000000;
    static final int h = size / 2;
    static float[] arr = new float[size];
    static long a = System.currentTimeMillis();
    private static Object mon = new Object();

    public static void main(String[] args) {
        for (int i = 0; i < size; i++) {
            arr[i] = 1;
            System.out.println(arr[i]);
        }
        method1();
        try {
            t1.join();
        }catch (ArrayStoreException e){
            e.printStackTrace();
        }



    }

    public static void method1() {
        for (int i = 0; i < size; i++) {
            arr[i] = (float) (arr[i] * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) * Math.cos(0.4f + i / 2));
        }
        System.currentTimeMillis();
        System.out.println(System.currentTimeMillis() - a);

    }

    public static void method2() {
        float a1 = 0;
        float a2 = 0;
        System.arraycopy(arr, 0, a1, 0, h);
        System.arraycopy(arr, h, a2, 0, h);
        Thread t1 = new Thread(() -> {
            for (int i = 0; i <= a1; i++) {
                arr[i] = (float) (arr[i] * Math.sin(0.2f + a1 / 5) * Math.cos(0.2f + a1 / 5) * Math.cos(0.4f + a1 / 2));
            }
        });
        synchronized (mon) {
            Thread t2 = new Thread(() -> {
                for (int i = 0; i <= a2; i++) {
                    arr[i] = (float) (arr[i] * Math.sin(0.2f + a2 / 5) * Math.cos(0.2f + a2 / 5) * Math.cos(0.4f + a2 / 2));
                }
            });
        }


        }
    }

