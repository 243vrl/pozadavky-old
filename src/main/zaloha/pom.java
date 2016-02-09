public List<String> getMozneNoveLetajici(){
        List<String> uzLetaji = getLetajici();
        List<String> vsichni = this.getUsersController().getUserNames();
        List<String> vratka = new ArrayList<>();
        for(String pom: vsichni){
            if(uzLetaji.contains(pom)) continue;
            vratka.add(pom);
        }
        return vratka;
    }