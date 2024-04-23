


import java.awt.event.KeyEvent;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.swing.JOptionPane;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;

import java.sql.Statement;
import java.util.Date;
import javax.swing.table.DefaultTableModel;
import net.proteanit.sql.DbUtils;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;


public class penjualan extends javax.swing.JFrame {
 private DefaultTableModel model=null;
 private PreparedStatement stat;
 private ResultSet rs;
 koneksi k = new koneksi();
    
     public penjualan() {
        initComponents();
        k.connec();
        tabelkosongdetail();
        tabelpenjualan();
        tabelkosongpelanggan();
        tabelkosongproduk();
        utama();
        Date date = new Date();
        SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy");
        txttglpenjualan.setText(s.format(date));
        txttotalharga.setText("0");
        txtnamapelanggan.requestFocus();
    }

    public void tabelkosongdetail() {
        //Create Table
        model = new DefaultTableModel();
        tabeldetailpenjualan.setModel(model);
        model.addColumn("Penjualan ID");
        model.addColumn("Produk ID");
        model.addColumn("Jumlah");
        model.addColumn("Harga");
        model.addColumn("Sub Total");
    }

    public void tabelpenjualan() {
        try {
            this.stat = k.getCon().prepareStatement("select * from penjualan");
            this.rs = this.stat.executeQuery();
            tabelpenjualan.setModel(DbUtils.resultSetToTableModel(rs));
            System.out.println("koneksi");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "tabel gagal di load");
        }
    }

    public void tabelkosongproduk() {
        //Create Table
        model = new DefaultTableModel();
        tabelproduk.setModel(model);
        model.addColumn("Produk ID");
        model.addColumn("Nama Produk");
        model.addColumn("Harga");
        model.addColumn("Stok");
    }

    public void tabelkosongpelanggan() {
        //Create Table
        model = new DefaultTableModel();
        tabelpelanggan.setModel(model);
        model.addColumn("Pelanggan ID");
        model.addColumn("Nama PLG");
        model.addColumn("Alamat");
        model.addColumn("No. TLP");
    }

    public void utama() {
        txtpelangganid.setText("");
        txtprodukid.setText("");
        txtnamaproduk.setText("");
        txtharga.setText("");
        txtjumlahproduk.setText("");
        autonumber();
    }

    public void clear() {
        txtpelangganid.setText("");
        txtnamapelanggan.setText("");
        txttotalharga.setText("0");
        txTampil.setText("0");
    }

    class FPenjualan {

        int PenjualanID, PelangganID, TtlHarga;
        String TanggalPenjualan;

        public FPenjualan() {
            this.PenjualanID = 0;
            this.TanggalPenjualan = txttglpenjualan.getText();
            this.TtlHarga = Integer.parseInt(txttotalharga.getText());
            this.PelangganID = 0;
        }
    }

    public void refreshTable() {
        try {
            this.stat = k.getCon().prepareStatement("select * from detailpenjualan");
            this.rs = this.stat.executeQuery();
            tabeldetailpenjualan.setModel(DbUtils.resultSetToTableModel(rs));
            System.out.println("Koneksi");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "tabel gagal di load");
        }
    }

    public void refresTabelPelanggan() {
        try {
            this.stat = k.getCon().prepareStatement("select * from Pelanggan where PelangganID like '%" + txtnamapelanggan.getText() + "%' OR NamaPelanggan like '%" + txtnamapelanggan.getText() + "%'");
            this.rs = this.stat.executeQuery();
            tabelpelanggan.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
        }
    }

    private void autonumber() {
        try {
            Connection c = k.getCon();
            Statement s = c.createStatement();
            String sql = "SELECT * FROM penjualan ORDER BY PenjualanID DESC";
            ResultSet r = s.executeQuery(sql);
            if (r.next()) {
                String NoFaktur = r.getString("PenjualanID").substring(2);
                String TR = "" + (Integer.parseInt(NoFaktur) + 1);
                String Nol = "";

                if (TR.length() == 1) {
                    Nol = "000";
                } else if (TR.length() == 2) {
                    Nol = "00";
                } else if (TR.length() == 3) {
                    Nol = "0";
                } else if (TR.length() == 4) {
                    Nol = "";
                }
                txtpenjualanid.setText("TR" + Nol + TR);
            } else {
                txtpenjualanid.setText("TR0001");
            }
            r.close();
            s.close();
        } catch (Exception e) {
            System.out.println("autonumber error");
        }
    }

    public void kosong() {
        DefaultTableModel model = (DefaultTableModel) tabeldetailpenjualan.getModel();

        while (model.getRowCount() > 0) {
            model.removeRow(0);
        }
    }

    public void inputpenjualan() {
        DefaultTableModel model = (DefaultTableModel) tabeldetailpenjualan.getModel();

        String penjID = txtpenjualanid.getText();
        String tglpenj = txttglpenjualan.getText();
        String idPLG = txtpelangganid.getText();
        String total = txttotalharga.getText();

        try {
            Connection c = k.getCon();
            String sql = "INSERT INTO penjualan VALUES (?, ?, ?, ?)";
            PreparedStatement p = c.prepareStatement(sql);
            p.setString(1, penjID);
            p.setString(2, tglpenj);
            p.setString(3, total);
            p.setString(4, idPLG);
            p.executeUpdate();
            p.close();
        } catch (Exception e) {
            System.out.println("simpan penjualan error");
        }

        try {
            Connection c = k.getCon();
            int baris = tabeldetailpenjualan.getRowCount();

            for (int i = 0; i < baris; i++) {
                String sql = "INSERT INTO detailpenjualan (PenjualanID, ProdukID, JumlahProduk, Harga, Subtotal) VALUES ('" + tabeldetailpenjualan.getValueAt(i, 0) + "','" + tabeldetailpenjualan.getValueAt(i, 1) + "','" + tabeldetailpenjualan.getValueAt(i, 2) + "','" + tabeldetailpenjualan.getValueAt(i, 3) + "','" + tabeldetailpenjualan.getValueAt(i, 4) + "')";
                PreparedStatement p = c.prepareStatement(sql);
                p.executeUpdate();
                p.close();
            }
        } catch (Exception e) {
            System.out.println("simpan detail penjualan error");
        }
        clear();
        utama();
        autonumber();
        kosong();
        txTampil.setText("Rp. 0");
    }

    public void refreshTabelProduk() {
        try {
            this.stat = k.getCon().prepareStatement("select * from produk where NamaProduk like '%" + txtnamaproduk.getText() + "%' OR ProdukID like '%" + txtnamaproduk.getText() + "%'");
            this.rs = this.stat.executeQuery();
            tabelproduk.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
        }
    }

    public void totalHarga() {
        int jumlahBaris = tabeldetailpenjualan.getRowCount();
        int totalHarga = 0;
        int jumlahProduk, hargaProduk;
        for (int i = 0; i < jumlahBaris; i++) {
            jumlahProduk = Integer.parseInt(tabeldetailpenjualan.getValueAt(i, 2).toString());
            hargaProduk = Integer.parseInt(tabeldetailpenjualan.getValueAt(i, 3).toString());
            totalHarga = totalHarga + (jumlahProduk * hargaProduk);
        }
        txttotalharga.setText(String.valueOf(totalHarga));
        txTampil.setText("Rp " + totalHarga + ",00");
    }

    public void loadData() {
        DefaultTableModel model = (DefaultTableModel) tabeldetailpenjualan.getModel();
        model.addRow(new Object[]{
            txtpenjualanid.getText(),
            txtprodukid.getText(),
            txtjumlahproduk.getText(),
            txtharga.getText(),
            txtsubtotal.getText()
        });
    }

    public void tambahDetailPenjualan() {
        int jumlah, harga, total;

        jumlah = Integer.valueOf(txtjumlahproduk.getText());
        harga = Integer.valueOf(txtharga.getText());
        total = jumlah * harga;

        txtsubtotal.setText(String.valueOf(total));

        loadData();
        totalHarga();
        clear2();
        txtnamaproduk.requestFocus();
    }

    public void clear2() {
        txtprodukid.setText("");
        txtnamaproduk.setText("");
        txtharga.setText("");
        txtjumlahproduk.setText("");
    }
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtpenjualanid = new javax.swing.JTextField();
        txtnamapelanggan = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtnamaproduk = new javax.swing.JTextField();
        txtharga = new javax.swing.JTextField();
        txtjumlahproduk = new javax.swing.JTextField();
        txtprodukid = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        tambahpelanggan = new javax.swing.JButton();
        tambahproduk = new javax.swing.JButton();
        txtsubtotal = new javax.swing.JTextField();
        txttotalharga = new javax.swing.JTextField();
        DetailPenjualan = new javax.swing.JButton();
        deletedetail = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabelpelanggan = new javax.swing.JTable();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabelproduk = new javax.swing.JTable();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabelpenjualan = new javax.swing.JTable();
        txtpelangganid = new javax.swing.JTextField();
        jScrollPane4 = new javax.swing.JScrollPane();
        tabeldetailpenjualan = new javax.swing.JTable();
        input = new javax.swing.JButton();
        cetaknota = new javax.swing.JButton();
        txTampil = new javax.swing.JTextField();
        txttglpenjualan = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setText("PENJUALAN");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("PenjualanID");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, -1, -1));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("Nama Pelanggan");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, -1, -1));

        txtpenjualanid.setEnabled(false);
        txtpenjualanid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtpenjualanidActionPerformed(evt);
            }
        });
        getContentPane().add(txtpenjualanid, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 50, 100, -1));

        txtnamapelanggan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtnamapelangganActionPerformed(evt);
            }
        });
        txtnamapelanggan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtnamapelangganKeyReleased(evt);
            }
        });
        getContentPane().add(txtnamapelanggan, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 80, 100, -1));

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setText("Tanggal Penjualan");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 50, -1, -1));

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setText("Nama Produk");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, -1, -1));

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setText("Harga");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 180, -1, -1));

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setText("Jumlah");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 210, -1, -1));

        txtnamaproduk.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtnamaprodukKeyReleased(evt);
            }
        });
        getContentPane().add(txtnamaproduk, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 150, 100, -1));

        txtharga.setEnabled(false);
        txtharga.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txthargaActionPerformed(evt);
            }
        });
        getContentPane().add(txtharga, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 180, 100, -1));

        txtjumlahproduk.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtjumlahprodukKeyTyped(evt);
            }
        });
        getContentPane().add(txtjumlahproduk, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 210, 100, -1));

        txtprodukid.setBackground(new java.awt.Color(153, 255, 255));
        getContentPane().add(txtprodukid, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 150, 100, -1));

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel8.setText("Sub Total");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 180, -1, -1));

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setText("Total Harga");
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 210, -1, -1));

        tambahpelanggan.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        tambahpelanggan.setText("TAMBAH PELANGGAN");
        tambahpelanggan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tambahpelangganActionPerformed(evt);
            }
        });
        getContentPane().add(tambahpelanggan, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 80, 160, -1));

        tambahproduk.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        tambahproduk.setText("TAMBAH PRODUK");
        tambahproduk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tambahprodukActionPerformed(evt);
            }
        });
        getContentPane().add(tambahproduk, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 150, 160, -1));

        txtsubtotal.setBackground(new java.awt.Color(255, 204, 204));
        getContentPane().add(txtsubtotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 180, 160, -1));

        txttotalharga.setBackground(new java.awt.Color(255, 204, 204));
        getContentPane().add(txttotalharga, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 210, 160, -1));

        DetailPenjualan.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        DetailPenjualan.setText("INPUT DETAIL PENJUALAN");
        DetailPenjualan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DetailPenjualanActionPerformed(evt);
            }
        });
        getContentPane().add(DetailPenjualan, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 280, 220, -1));

        deletedetail.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        deletedetail.setText("DELETE DETAIL PENJUALAN");
        deletedetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deletedetailActionPerformed(evt);
            }
        });
        getContentPane().add(deletedetail, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 280, 230, -1));

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel10.setText("TABEL PELANGGAN");
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 20, -1, -1));

        tabelpelanggan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tabelpelanggan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabelpelangganMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tabelpelanggan);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(512, 40, 380, 90));

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel11.setText("TABEL PRODUK");
        getContentPane().add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 150, -1, -1));

        tabelproduk.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tabelproduk.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabelprodukMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tabelproduk);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(512, 170, 380, 90));

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel12.setText("TABEL PENJUALAN");
        getContentPane().add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 270, -1, -1));

        tabelpenjualan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane3.setViewportView(tabelpenjualan);

        getContentPane().add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(512, 290, 380, 100));

        txtpelangganid.setBackground(new java.awt.Color(153, 255, 255));
        getContentPane().add(txtpelangganid, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 80, 100, -1));

        tabeldetailpenjualan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tabeldetailpenjualan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabeldetailpenjualanMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tabeldetailpenjualan);

        getContentPane().add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 320, 490, 110));

        input.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        input.setText("INPUT");
        input.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputActionPerformed(evt);
            }
        });
        getContentPane().add(input, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 450, 110, -1));

        cetaknota.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        cetaknota.setText("CETAK DATA");
        cetaknota.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cetaknotaActionPerformed(evt);
            }
        });
        getContentPane().add(cetaknota, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 450, -1, -1));

        txTampil.setBackground(new java.awt.Color(255, 102, 102));
        txTampil.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        txTampil.setText("RP.0");
        txTampil.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txTampilActionPerformed(evt);
            }
        });
        getContentPane().add(txTampil, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 440, 80, 40));

        txttglpenjualan.setEnabled(false);
        getContentPane().add(txttglpenjualan, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 50, 160, -1));

        setSize(new java.awt.Dimension(905, 545));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void txtnamapelangganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtnamapelangganActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtnamapelangganActionPerformed

    private void txtpenjualanidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtpenjualanidActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtpenjualanidActionPerformed

    private void tabelprodukMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelprodukMouseClicked
        // TODO add your handling code here:
        int r =tabelproduk.getSelectedRow();
        String ProdukID = tabelproduk.getValueAt(r,0).toString();
            String NamaPrd = tabelproduk.getValueAt(r,1).toString();
                String Hrg = tabelproduk.getValueAt(r,2).toString();
                    String Ttl = tabelproduk.getValueAt(r,3).toString();
                    txtprodukid.setText(ProdukID);
                      txtnamaproduk.setText(NamaPrd);
                        txtharga.setText(Hrg);
                          txtjumlahproduk.requestFocus();
    }//GEN-LAST:event_tabelprodukMouseClicked

    private void txtnamaprodukKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtnamaprodukKeyReleased
        // TODO add your handling code here:
        refreshTabelProduk();
        txtprodukid.setText("");
    }//GEN-LAST:event_txtnamaprodukKeyReleased

    private void txtnamapelangganKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtnamapelangganKeyReleased
        // TODO add your handling code here
        refresTabelPelanggan();
        txtpelangganid.setText("");
    }//GEN-LAST:event_txtnamapelangganKeyReleased

    private void tabelpelangganMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelpelangganMouseClicked
        // TODO add your handling code here:
        int r =tabelpelanggan.getSelectedRow();
        String PlgID = tabelpelanggan.getValueAt(r,0).toString();
            String NamaPlg = tabelpelanggan.getValueAt(r,1).toString();
                String Alamat = tabelpelanggan.getValueAt(r,2).toString();
                    String Tlp = tabelpelanggan.getValueAt(r,3).toString();
                    txtprodukid.setText(PlgID);
                      txtnamaproduk.setText(NamaPlg);
                          txtjumlahproduk.requestFocus();
    }//GEN-LAST:event_tabelpelangganMouseClicked

    private void DetailPenjualanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DetailPenjualanActionPerformed
        // TODO add your handling code here:
         DefaultTableModel model = (DefaultTableModel) tabeldetailpenjualan.getModel();
        int row = tabeldetailpenjualan.getSelectedRow();
        model.removeRow(row);
       tambahDetailPenjualan();
    }//GEN-LAST:event_DetailPenjualanActionPerformed

    private void deletedetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deletedetailActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tabeldetailpenjualan.getModel();
        int row = tabeldetailpenjualan.getSelectedRow();
        model.removeRow(row);
       totalHarga();
    }//GEN-LAST:event_deletedetailActionPerformed

    private void tambahpelangganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tambahpelangganActionPerformed
        // TODO add your handling code here
        pelanggan a = new pelanggan();
        a.setVisible(true);
    }//GEN-LAST:event_tambahpelangganActionPerformed

    private void tambahprodukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tambahprodukActionPerformed
        // TODO add your handling code here:
        produk a = new produk();
        a.setVisible(true);
    }//GEN-LAST:event_tambahprodukActionPerformed

    private void txtjumlahprodukKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtjumlahprodukKeyTyped
        // TODO add your handling code here:
        // validasi hanya input angka
        char karakter = evt.getKeyChar();
        if (!(((karakter >= '0') && (karakter <= '9') || (karakter == KeyEvent.VK_BACK_SPACE) || (karakter == KeyEvent.VK_DELETE)))) {
            JOptionPane.showMessageDialog(null, "Hanya boleh input angka");
            evt.consume();
        }
    }//GEN-LAST:event_txtjumlahprodukKeyTyped

    private void cetaknotaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cetaknotaActionPerformed
        // TODO add your handling code here:
        // KODING CETAK NOTA
        try {
            File namafile = new File("src/laporan/cetaklaporan.jasper");
            JasperPrint jp = JasperFillManager.fillReport(namafile.getPath(), null, k.getCon());
            JasperViewer.viewReport(jp,false);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }//GEN-LAST:event_cetaknotaActionPerformed

    private void inputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputActionPerformed
        // TODO add your handling code here:
        inputpenjualan();
        tabelpenjualan();
    }//GEN-LAST:event_inputActionPerformed

    private void tabeldetailpenjualanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabeldetailpenjualanMouseClicked
        // TODO add your handling code here:
         int r =tabeldetailpenjualan.getSelectedRow();
        String jumlah = tabeldetailpenjualan.getValueAt(r,0).toString();
            String harga = tabeldetailpenjualan.getValueAt(r,1).toString();
                String total = tabeldetailpenjualan.getValueAt(r,2).toString();
                    txtsubtotal.setText(total);
                      txtharga.setText(harga);
                          txtjumlahproduk.requestFocus();
    }//GEN-LAST:event_tabeldetailpenjualanMouseClicked

    private void txTampilActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txTampilActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_txTampilActionPerformed

    private void txthargaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txthargaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txthargaActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(penjualan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(penjualan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(penjualan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(penjualan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new penjualan().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton DetailPenjualan;
    private javax.swing.JButton cetaknota;
    private javax.swing.JButton deletedetail;
    private javax.swing.JButton input;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable tabeldetailpenjualan;
    private javax.swing.JTable tabelpelanggan;
    private javax.swing.JTable tabelpenjualan;
    private javax.swing.JTable tabelproduk;
    private javax.swing.JButton tambahpelanggan;
    private javax.swing.JButton tambahproduk;
    private javax.swing.JTextField txTampil;
    public static javax.swing.JTextField txtharga;
    public static javax.swing.JTextField txtjumlahproduk;
    public static javax.swing.JTextField txtnamapelanggan;
    public static javax.swing.JTextField txtnamaproduk;
    public static javax.swing.JTextField txtpelangganid;
    private javax.swing.JTextField txtpenjualanid;
    public static javax.swing.JTextField txtprodukid;
    private javax.swing.JTextField txtsubtotal;
    private javax.swing.JTextField txttglpenjualan;
    private javax.swing.JTextField txttotalharga;
    // End of variables declaration//GEN-END:variables
}
