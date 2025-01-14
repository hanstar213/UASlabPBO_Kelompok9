
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.InputMismatchException;
import java.util.Locale;
import java.util.Scanner;

public class CustomerDriver extends Driver {
    private Customer customer;
    private ListBarang daftarBarang;
    private Transaksi transaksi;

    public CustomerDriver(Customer customer){
        super(customer);
        this.customer = customer;
        this.daftarBarang = new ListBarang();
        this.daftarBarang.loadFromFile("Barang.txt");
        this.transaksi = new Transaksi(null, null, null, null, null, null);
    }

    @Override
    public void menu(){
        System.out.println("Customer: " + customer);
        if (customer != null) {
            System.out.println("Keranjang: " + customer.getKeranjang());
        }
        Scanner input = new Scanner(System.in);

        int is_continue=1;
        while(is_continue==1){
            Main.clearScreen();
            System.out.println("\nMenu Pelanggan:");
            System.out.println("1) Lihat Barang");
            System.out.println("2) Tambah ke Keranjang");
            System.out.println("3) Checkout");
            System.out.println("4) Riwayat Transaksi");
            System.out.println("5) Keluar");
            System.out.print("Pilihan: ");
            int pilihan = Main.getSafeIntInput(input);
            input.nextLine();
            
            switch(pilihan){
                case 1 :
                    // Menu Menampilkan seluruh barang
                    System.out.println("=======================\n    Daftar Barang\n=======================");
                    showItems();
                    break;
                case 2 :
                    // Menu menambahkan barang ke keranjang
                    addToCartMenu(input);
                    break;
                case 3 :
                    // Menu Chechkout
                    checkoutMenu(input);
                    break;
                case 4 :
                    // Menampilkan riwayat transaksi customer
                    transaksi.showTransactionsByCustomer(customer.getId());
                    System.out.println("\n<Tekan enter unutuk berhenti menampilkan riwayat>");
                    break;
                case 5 :
                    System.out.println("<Terima kasih telah berbelanja>");  // keluar dari program
                    is_continue = 0;
                    break;
                default:
                    System.out.println("<Pilihan tidak valid>.");
                    break;
            }
            input.nextLine(); // input enter
        }
    }

    // method untuk menampilkan barang
    public void showItems(){
        Main.clearScreen();
        DecimalFormat df = new DecimalFormat("#,###", new java.text.DecimalFormatSymbols(Locale.GERMANY));

        // Menampilkan data barang dalam format tabel
        System.out.println("+------------+------------------------------------------+-----------------+------------+");
                    System.out.printf("| %-10s | %-40s | %-15s | %-10s |\n", "ID Barang", "Nama Barang", "Harga", "Stok");
                    System.out.println("+------------+------------------------------------------+-----------------+------------+");                    
                    for (Barang barang : daftarBarang.getDaftarBarang()) {
                        String hargaBarang = df.format((barang.getHargaBarang())); // Format harga dengan pemisah ribuan
                        System.out.printf("| %-10s | %-40s | Rp%-13s | %-10d |\n",
                                barang.getIdBarang(),
                                barang.getNamaBarang(),
                                hargaBarang,
                                barang.getStok());
                            System.out.println("+------------+------------------------------------------+-----------------+------------+");
                    }
        System.out.println("\n<Tekan enter untuk berhenti menampilkan daftar>");
    }

    // method menu menambahkan barang ke dalam keranjang
    public int addToCartMenu(Scanner input){
        while (true) { 
            Main.clearScreen();
            System.out.println("=======================\n        Keranjang\n=======================");
            System.out.println("1) Lihat daftar barang");
            System.out.println("2) Tambah barang ke keranjang");
            System.out.println("3) Kembali");
            System.out.print("Masukkan pilihan : ");
            int option = Main.getSafeIntInput(input);
            input.nextLine();
            if(option == 1){
                showItems();
            } else if (option == 2) {
                doAddToCart(input);
            } else if (option == 3) {
                return 0;
            } else{
                System.out.println("Pilihan tidak valid");
            }
            input.nextLine();
            Main.clearScreen();
        }
    }

    // Method menambah barang ke keranjang
    public void doAddToCart(Scanner input){
        Main.clearScreen();
        System.out.print("Masukkan id barang atau nama barang yang ingin dicari : ");
        String cartInput = input.nextLine();

        boolean isValid = false;
        int jumlah = 0;
        while (!isValid) {
            try {
                System.out.print("Masukkan jumlah : ");
                jumlah = input.nextInt(); // Membaca input angka
                isValid = true; // Jika berhasil membaca angka, keluar dari loop
            } catch (InputMismatchException e) {
                System.out.println("Input tidak valid! Harap masukkan angka");
                input.nextLine(); // Membersihkan input yang salah
                input.nextLine();
                Main.clearScreen();
            }
        }

        input.nextLine();
        Barang barangDipilih = null;
        for (Barang barang : daftarBarang.getDaftarBarang()) {
            if (barang.getIdBarang().equals(cartInput) || barang.getNamaBarang().equals(cartInput)) {
                barangDipilih = barang;
                break;
                }
            }
            
            DecimalFormat df = new DecimalFormat("#,###", new java.text.DecimalFormatSymbols(Locale.GERMANY));
            String hargaBarang = df.format(barangDipilih.getHargaBarang()); // Format harga dengan pemisah ribuan
            String totalHarga = df.format((jumlah*barangDipilih.getHargaBarang())); // Format harga dengan pemisah ribuan
            if (barangDipilih != null && jumlah <= barangDipilih.getStok()) {
                // Tampilkan detail barang yang dipilih sebelum menambahkannya ke keranjang
                System.out.println("\nBarang yang Anda pilih :");
                System.out.println("ID Barang\t\t : " + barangDipilih.getIdBarang());
                System.out.println("Nama Barang\t\t : " + barangDipilih.getNamaBarang());
                System.out.println("Harga Barang\t\t : Rp" + hargaBarang);
                System.out.println("Jumlah Barang\t\t : " + jumlah);
                System.out.println("Total Harga\t\t : Rp" + totalHarga);
                
                System.out.print("\nApakah Anda yakin ingin menambahkan barang ini ke keranjang? (y/n): ");
                String konfirmasi = input.nextLine();

                if (konfirmasi.equalsIgnoreCase("y")) {
                    // Tambahkan ke keranjang
                    customer.getKeranjang().addToCart(customer, barangDipilih, jumlah);

                    System.out.println("\n<Barang berhasil ditambahkan ke keranjang>");
                    } else if(konfirmasi.equalsIgnoreCase("n")) {
                        System.out.println("\n<Penambahan barang ke keranjang dibatalkan>");
                    } else {
                        System.out.println("\n<Pilihan tidak valid>");
                    }
                } else {
                    System.out.println("\n<Barang tidak ditemukan atau stok tidak mencukupi>");
                }
    }
    
    public int checkoutMenu(Scanner input){
        // Checkout
        while(true){
            Main.clearScreen();
            System.out.println("=======================\n    Checkout Barang\n=======================");
            System.out.println("1) Checkout Barang");
            System.out.println("2) Batalkan Checkout");
            System.out.println("3) Kembali");
            System.out.print("Masukkan Pilihan : ");
            int option = Main.getSafeIntInput(input);
            input.nextLine();
            if (option == 1) {
                doCheckout(input);
            } else if (option == 2) {
                cancelCheckout(input);
            } else if (option == 3) {
                return 0;
            } else{
                System.out.println("<Pilihan tidak valid>");
            }
            input.nextLine();
            Main.clearScreen();
        }
    }

    // Method melakukan checkout
    public int doCheckout(Scanner input) {
        Main.clearScreen();
        customer.getKeranjang().showCart(customer);
        if (isCartEmpty(customer.getId())) {
            System.out.println("Tidak ada barang di dalam daftar keranjang.");
            return 0;
        }
    
        System.out.print("Masukkan ID barang yang ingin di checkout: ");
        String idBarang = input.nextLine();
        
        synchronizeKeranjang();
        boolean found = false;
        Barang barangDipilih = null;
        for (Barang barang : customer.getKeranjang().getDaftarKeranjang()) {
            if (barang.getIdBarang().equals(idBarang)) {
                barangDipilih = barang;
                found = true;
                break;
            }
        }

        if(found){
            // Konfirmasi sebelum transaksi
            System.out.print("\nApakah Anda yakin ingin melakukan checkout barang ini? (y/n): ");
            String konfirmasi = input.nextLine();
            if (konfirmasi.equalsIgnoreCase("y")) {
                System.out.println("\nMetode pembayaran:");
                System.out.println("1) QRIS");
                System.out.println("2) Bank");
                System.out.println("3) COD");
                System.out.print("Masukkan pilihan: ");
                int option = Main.getSafeIntInput(input);
                input.nextLine();
            
                Pembayaran metodePembayaran = null;
                String metodePembayaranStr = "";
                switch (option) {
                    case 1:
                        metodePembayaran = new QRIS("Q000"); // Membuat objek QRIS
                        metodePembayaranStr = "QRIS";
                        break;
                    case 2:
                        metodePembayaran = new Bank("M000"); // Membuat objek Bank
                        metodePembayaranStr = "Bank";
                        break;
                    case 3:
                        metodePembayaran = new COD("D000"); // Membuat objek COD
                        metodePembayaranStr = "COD";
                        break;
                    default:
                        System.out.println("<Pilihan tidak valid>");
                        return 0;
                }
            
                // Tampilkan konfirmasi setelah memilih metode pembayaran
                System.out.println("\nMetode pembayaran yang Anda pilih: " + metodePembayaranStr);
                System.out.print("Apakah Anda yakin ingin melanjutkan dengan metode pembayaran ini? (y/n): ");
                String konfirmasiMetodePembayaran = input.nextLine();
                if (!konfirmasiMetodePembayaran.equalsIgnoreCase("y")) {
                    System.out.println("<Pembayaran dibatalkan>");
                    return 0;
                }
                    
                // Konfirmasi untuk transaksi
                System.out.print("\nApakah Anda yakin ingin melakukan transaksi untuk barang ini? (y/n): ");
                String konfirmasiTransaksi = input.nextLine();
                if (!konfirmasiTransaksi.equalsIgnoreCase("y")) {
                    System.out.println("<Transaksi dibatalkan>");
                    return 0;
                }
                    
                Main.clearScreen();
                Transaksi transaksi = new Transaksi(null, null, null, null, null, null); // Dummy untuk method statis
                transaksi.doTransaksi(customer, barangDipilih, metodePembayaran);
                System.out.println("\n<Transaksi Berhasil>");
            
                // Hapus barang dari keranjang
                customer.getKeranjang().removeFromCart(customer.getId(), idBarang);
                // Kurangi stok barang
                daftarBarang.updateStok(idBarang, barangDipilih.getStok());   
                } else if (konfirmasi.equalsIgnoreCase("n")) {
                    System.out.println("\n<Chehkout dibatalkan>");
                } else{
                    System.out.println("\n<Pilihan tidak valid>");
                }

        } else if(!found){
            System.out.println("\n<Barang dengan id " + idBarang + " tidak ditemukan>");
        }

        return 0;
    }
    
    public void synchronizeKeranjang() {
        String fileCart = "Cart.txt";
        customer.getKeranjang().getDaftarKeranjang().clear(); // Bersihkan keranjang sebelum memuat ulang
        try (BufferedReader reader = new BufferedReader(new FileReader(fileCart))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    String customerId = parts[0];
                    String idBarang = parts[1];
                    String namaBarang = parts[2];
                    double hargaBarang = Double.parseDouble(parts[3]);
                    int jumlahBarang = Integer.parseInt(parts[4]);

                    if (customerId.equals(customer.getId())) {
                        Barang barang = new Barang(idBarang, namaBarang, hargaBarang, jumlahBarang);
                        customer.getKeranjang().getDaftarKeranjang().add(barang);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Gagal memuat keranjang dari file: " + e.getMessage());
        }
    }

    // Method untuk mengecek keranjang kosong atau tidak
    public boolean isCartEmpty(String customerId) {
        String fileCart = "Cart.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(fileCart))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5 && parts[0].equals(customerId)) {
                    return false;
                }
            }
        } catch (IOException e) {
            System.out.println("Gagal membaca file Cart.txt: " + e.getMessage());
        }
        return true;
    }

    public int cancelCheckout(Scanner input) {
        // Membatalkan checkout
        Main.clearScreen();
        customer.getKeranjang().showCart(customer);
        if (isCartEmpty(customer.getId())) {
            System.out.println("Tidak ada barang di dalam daftar keranjang.");
            return 0;
        }
    
        System.out.print("Masukkan ID barang yang ingin dibatalkan checkout : ");
        String idBarang = input.nextLine();

        synchronizeKeranjang();
        boolean found = false;
        Barang barangDipilih = null;
        for (Barang barang : customer.getKeranjang().getDaftarKeranjang()) {
            if (barang.getIdBarang().equals(idBarang)) {
                barangDipilih = barang;
                found = true;
                break;
            }
        }

        if(found){
             // Konfirmasi sebelum membatalkan checkout
            System.out.print("\nApakah Anda yakin ingin membatalkan checkout untuk barang ini? (y/n): ");
            String konfirmasi = input.nextLine();
            if (konfirmasi.equalsIgnoreCase("y")) {
                // Jika barang ditemukan, hapus barang dari keranjang
                customer.getKeranjang().removeFromCart(customer.getId(), idBarang);
                daftarBarang.updateStokAfterCancel(idBarang, barangDipilih.getStok());
                System.out.println("<Barang dengan ID " + idBarang + " berhasil dibatalkan dari keranjang>");
                return 0;
            } else if (konfirmasi.equalsIgnoreCase("n")){
                System.out.println("\n<Pembatalan checkout dibatalkan>");
            } else{
                System.out.println("\n<Pilihan tidak valid>");
            }

        } else if (!found) {
            System.out.println("\n<Barang dengan id "+ idBarang + " tidak ditemukan>");
        }
        
        return 0;
    }
    
}
