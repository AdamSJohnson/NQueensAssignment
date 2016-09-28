import java.util.*;
import java.io.*;

public class NQueens{
    public static void main(String[] args){
    
        //NOTES:
        //  Some of the parameters are left overs from the first
        //  iteration of NQueens. It used to be a fitness and age
        //  based parent selection. Some of the variables are now 
        //  statically coded as to allign with the assignment.
        
        //accept in 3 parameters:
        // size, ps, pss
        // size is the N in n queens look it up
        // ps is the size of the population
        // pss is the parent sample size
        // al is the age limit
        int size = Integer.parseInt(args[0]);
        int ps = size * 10;
        int pss = ps / 10;
        if(pss%2 == 1)
            pss++;
        
        //the age limit is a remnant of the code I wrote before the assignment
        //I left it in for ease of use, since generational limit is 1000
        //an age limit of 9000 should not disrupt the score
        int al = 9000;
        
        //construct our population array list
        ArrayList<QObject> population = popBuilder(ps, size);
        
        //setup the qfinder 
        QFinder qf = new QFinder(population, ps, pss, al);
        //System.out.println(qf.population);
        
        //run the qfinder
        try{
            qf.run();
        } catch (Exception e){
            System.exit(1);
        }

    }
    
    //randomizer mixes up the array inorder to have a random population
    public static void randomizer(int[] target){
        Random rand = new Random();
        //swap things randomly
        for(int i = 0; i<target.length *2; i++){
            //grab random positions in the array
            int a = rand.nextInt(target.length);
            int b = rand.nextInt(target.length);
            
            //grab the values from each position
            int first = target[a];
            int second = target[b];
            
            //swap the values
            target[a] = second;
            target[b] = first;
        }
    }
    
    //this just creates the inital populatiion
    public static ArrayList<QObject> popBuilder(int ps, int size){
        ArrayList<QObject> result = new ArrayList<>();
        for(int i = 1; i <= ps; i++){
            int [] temp = genotypeBuilder(size);
            result.add(new QObject(temp, fitness(temp)));
        }
        return result;
    }
    
    //creates the single individual and randomizes it
    public static int[] genotypeBuilder(int size){
        int[] result = new int[size];
        for(int i = 0; i < size; i ++){
            result[i] = i + 1;
        }
        randomizer(result);
        return result;
    }
    
    //this is the fitness function it tells us how good our thingy does
    public static double fitness(int[] a){
        int collisions = 0;
        double e = .00001;
        
        for(int i = 0; i < a.length; i ++){
            //check for collisions on the first half of the array
            for(int j = 0 ; j < i  ; j++){
                
                if(a[i] + (i-j) == a[j])
                    collisions++;
                if(a[i] - (i -j) == a[j])
                    collisions++;
            }
            
            //removed this section of diagonal checker after I realized
            //you only need to check the half of the array up to the position you are
            //checking
            
            /*
            for(int j = i+1; j < a.length; j++){
                if(a[i] + (i- j) == a[j])
                    collisions++;
                if(a[i] - (i -j) == a[j])
                    collisions++;
            }
            */
        }
        //System.out.println(Arrays.toString(a) + " " +  (collisions));
        return 1 / (collisions + e);
    }
}


//q object is an individual specimen
//it holds the fitness score and the genotype
class QObject{
    double fitness;
    int[] genotype;
    int age;
    
    public QObject(int[] a, double fitness){
        this.fitness = fitness;
        genotype = a;
        age = 0;
    }
    
    public double fitness(){
        return fitness;
    }
    
    public int[] genotype(){
        return genotype;
    }
    
    @Override
    public String toString(){
        return Arrays.toString(genotype);
    }
    
}

//qfinder is the meat of the algorithm this does the work
//creates and find the solution to a NQueen board
class QFinder{
    
    ArrayList<QObject> population;
    int size;
    int numberOfParents;
    int ageLimit;
    
    public QFinder(ArrayList<QObject> a, int s, int parents, int al){
        population = a;
        size = s;
        numberOfParents = parents;
        ageLimit = al;
    }
    
    public void run(){
        
        boolean s = true;
        int count = 0;
        while(s){
            //sort arraylist by fitness
            this.fitnessSort();
            //System.out.print(population);
            //easy enough  to find a winning solution
            if(population.get(0).fitness() > 1){
            
                System.out.println("FOUND A MATCH");
                System.out.println(population.get(0));
                
                //need to print to a file by appending it
                
                
                try{
                    File writeTo = new File("results1.txt");
                    
                    //create the file if it does not exist
                    if(!writeTo.exists()){
                        writeTo.createNewFile();
                    }
                    
                    //Here true is to append the content to file
                    FileWriter fw = new FileWriter(writeTo,true);
                    
                    //BufferedWriter writer give better performance
                    BufferedWriter bw = new BufferedWriter(fw);
                    bw.write(population.get(0) + ", " + count + "\n");
                    
                    //Closing BufferedWriter Stream
                    bw.close();
                    System.exit(0);
                } catch (FileNotFoundException e){
                    System.exit(1);
                } catch (IOException e2){
                    System.exit(1);
                }
                
            }

            if(count > 1000){
                System.out.println("NO MATCH FOUND");
                try{
                    File writeTo = new File("results1.txt");
                    
                    //create the file if it does not exist
                    if(!writeTo.exists()){
                        writeTo.createNewFile();
                    }
                    
                    //Here true is to append the content to file
                    FileWriter fw = new FileWriter(writeTo,true);
                    
                    //BufferedWriter writer give better performance
                    BufferedWriter bw = new BufferedWriter(fw);
                    bw.write("Failed" + "; " + count + "\n");
                    
                    //Closing BufferedWriter Stream
                    bw.close();
                    System.exit(0);
                } catch (FileNotFoundException e){
                    System.exit(1);
                } catch (IOException e2){
                    System.exit(1);
                }
                System.exit(0);
            }
            //update the age of all subjects
            /*
            for(int i = 0; i < population.size(); i++){
                population.get(i).age++;
                if(population.get(i).age > this.ageLimit)
                    population.remove(i);
            }
            */
            
            //kill of any items that do not meet the fitness cut off
            //only keep the initial population size amount
            while(population.size() > this.size){
                //remove things until we have the size
                population.remove(population.size()-1);
            }
            
           
            //keeps track of how many times we loop
            count++;
            System.out.print(population.get(0).fitness() + " ");
            System.out.print(population.size() + " ");
            System.out.println(count);
            
            //find parents
            QObject[] parents = new QObject[numberOfParents];
            int[] picks = new int[numberOfParents];
            
            //patent pending parent finding algorithm this alforithm pits 3
            //solutions against each other, the higher one wins, if there is a 
            //tie a always wins, the if b ties c b wins
            
            //a and b and c are randomly selected
            for(int i = 0; i < numberOfParents; i++){
                //doh, just make the array full of -1's because 0 is a valid
                //choice
                for(int j = 0; j < picks.length; j ++)
                    picks[i]--;
                
                //wooo random pickings (LOTTERY TIME)
                Random rand = new Random();
                int a = rand.nextInt(this.size);
                int b = rand.nextInt(this.size);
                int c = rand.nextInt(this.size);
                
                //make sure the 3 individuals aren't inside the array already
                for(int j = 0; j < picks.length; j++){
                    while(picks[j] == a){
                        a = rand.nextInt(this.size);
                        j = 0;
                    }
                }
                for(int j = 0; j < picks.length; j++){
                    while(picks[j] == b){
                        b = rand.nextInt(this.size);
                        j = 0;
                    }
                }
                for(int j = 0; j < picks.length; j++){
                    while(picks[j] == c){
                        c = rand.nextInt(this.size);
                        j = 0;
                    }
                }
                
                //fitness fighting
                //because the list is sorted already we can assume this
                //a being < than b and c means a has a higher fitness
                //b being < than a and c means b has a higher fitness
                //c being < than b and c means c has the higher fitness
                //in the event of a tie a wins over c and b wins over c
                if( a <= b && a <= c)
                    picks[i] = a;
                else if( b <= c)
                    picks[i] = b;
                else
                    picks[i] = c;
            }
            //sort the array
            Arrays.sort(picks);
            //System.out.println(Arrays.toString(picks));
            //once we have the parents we can do recombination with the parents
            for(int i = 1; i < picks.length; i = i + 2){
                //System.out.println("COMBINING");
                combine(population.get(picks[i-1]),population.get( picks[i]));
            }
        }
        

    }
    
    
    //the combination function woooo
    // Parameters: 
    //      a and b are the two QObjects to merge together
    private void combine(QObject a, QObject b){
        //pick a combination point
        Random r = new Random();
        
        //find the cross point between 0 and the length of the genotype - 2
        int cp = r.nextInt(a.genotype().length - 1) ;
        
        //if cp is the starting point subtract 1
        if(cp == 0){
            cp++;
        }
        
        //create the new individuals
        int[] baby1 = new int[a.genotype().length];
        int[] baby2 = new int[a.genotype().length];
        for(int i = 0; i < cp; i++){
            baby1[i] = a.genotype()[i];
            baby2[i] = b.genotype()[i];
        }
        
        //from the cp point go through each element in the other array and check 
        //if the baby contains it already if not add it in at the cp point
        int newCP = cp;
        while(newCP != baby1.length){
            for(int i = 0; i < b.genotype.length; i++){
                if(!contains(baby1, b.genotype[i])){
                    baby1[newCP] = b.genotype[i];
                    newCP++;
                }
            }
        }
        newCP = cp;
        while(newCP != baby2.length){
            for(int i = 0; i < a.genotype.length; i++){
                if(!contains(baby2, a.genotype[i])){
                    baby2[newCP] = a.genotype[i];
                    newCP++;
                }
            }
        }

        //System.out.println(Arrays.toString(baby1));
        
        //random mutation
        mutate(baby1);
        mutate(baby2);
        population.add(new QObject(baby1, fitness(baby1)));
        population.add(new QObject(baby1, fitness(baby2)));
        //System.out.println("Added new babies");
    }
    
    
    
    //checks if an array contains the value 
    //returns true if the value is in a
    //return false if the value is not in a
    private boolean contains(int[] a, int value){
        for(int i = 0; i < a.length; i++){
            if(a[i] == value)
                return true;
        }
        
        return false;
    }
    
    //mutate takes an array and has a 10% chance of swapping two random positions in the array
    //parameters:
    //   a - the array to mutate
    public void mutate(int[] a){
        Random r = new Random();
        int b = r.nextInt(a.length);
        int c = r.nextInt(a.length);
        if(r.nextInt(100) > 10){
            int temp = a[b];
            a[b] = a[c];
            a[c] = temp;
        }
    }
    
    //sorts the array of QObjects
    private void fitnessSort(){
        QObject[] a = new QObject[this.population.size()];
        QObject insert = this.population.remove(population.size()-1);
        a[0] = insert;
        int aSize = 1;
        while(!population.isEmpty()){
            insert = this.population.remove(population.size()-1);


            int insertPos = aSize;

            for(int i = 0; i < aSize; i++){
                if(insert.fitness() > a[i].fitness()){
                    insertPos = i;
                    i = aSize + 1;
                }
            }
            if(insertPos == aSize){
                a[insertPos] = insert;
                aSize++;
                
            } else {
                insert(a, insert, insertPos, aSize);
                aSize++;
            }
        }
        for(int i = 0; i < a.length; i++)
            this.population.add(a[i]);
        
    }
    
    //insert takes QObject b and inserts the object
    //into the pos value of a
    //parameters
    //   a - the target array
    //   b - what to insert
    //   pos - where to insert it
    //   aSize - the "end" of the array
    private void insert(QObject[] a, QObject b, int pos, int aSize){
        for(int i = aSize; i > pos ; i --){
            a[i]= a[i - 1];
        }
        a[pos ] = b;
    }
    
    
    //calulate the fitness of an array a
    public static double fitness(int[] a){
        int collisions = 0;
        double e = .00001;
        
        for(int i = 0; i < a.length; i ++){
            //check for collisions on the first half of the array
            for(int j = 0 ; j <= i - 1 ; j++){
                
                if(a[i] + (i-j) == a[j])
                    collisions++;
                if(a[i] - (i -j) == a[j])
                    collisions++;
            }
            
            //removed this section of diagonal checker after I realized
            //you only need to check the half of the array up to the position you are
            //checking
            /*
            for(int j = i+1; j < a.length; j++){
                if(a[i] + (i- j) == a[j])
                    collisions++;
                if(a[i] - (i -j) == a[j])
                    collisions++;
            }
            */
        }
        
        return 1 / (collisions + e);
    }
}