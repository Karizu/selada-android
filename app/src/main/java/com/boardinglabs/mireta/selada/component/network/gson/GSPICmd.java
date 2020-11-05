package com.boardinglabs.mireta.selada.component.network.gson;

public class GSPICmd {

    public String cmd;
    public int tid;
    public int sts;
    public GSPIMsg msg;

    public class GSPIMsg {
        public String msg;
        public String rc;
        public String ket;
        public String ref;
        public String nama;
        public String idpel;
        public String nomtr;
        public String tarif;
        public String daya;
        public String admin;
        public String jml_bln_byr;
        public String jml_tunggakan_bln;
        public String bl_thn;
        public int total_bayar;
        public int rp_tag;
        public int denda;
        public String stand_meter;

        //ADD OBJECT FOR PDAM RESPONSE
        public String nopel;
        public String periode;
        public String tagihan;
        public String total;

        //ADD OBJECT FOR BPJSKS
        public String nova;
        public String jbln;
        public String jpst;
        public String premi;


    }
}
