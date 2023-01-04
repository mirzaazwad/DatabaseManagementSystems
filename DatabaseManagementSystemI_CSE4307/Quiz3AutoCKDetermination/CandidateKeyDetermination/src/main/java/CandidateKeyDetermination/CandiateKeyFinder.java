package CandidateKeyDetermination;

import java.util.*;
//this class acts as the most important class for candidate key generation
public class CandiateKeyFinder {
    private List<String>attributes;
    public AttributeSetOperations attributeSetOperations;
    public CandiateKeyFinder(List<String>inputAttributeList){
        this.attributes=inputAttributeList;
        attributeSetOperations=new AttributeSetOperations();
    }

    public List<String>getAttributes(){
        return this.attributes;
    }

    //First candidate key generated by elimination process
    public String getACandidateKey()
    {
        CandidateKeyTest candidateKeyTest=new CandidateKeyTest(this.attributes);
        String input=String.valueOf(AttributeClosure.AllAttributeSuperKey(this.attributes).charAt(0));
        while(!new AttributeClosure(input).getAttributeClosure().equals(AttributeClosure.AllAttributeSuperKey(this.attributes)))
        {
            input=input+findNext(input);
        }
        input=candidateKeyTest.CheckProperSubset(input);
        for(int currentPositionInCandidateKey=0;currentPositionInCandidateKey<input.length();currentPositionInCandidateKey++){
            attributeSetOperations.primeAttributes.add(String.valueOf(input.charAt(currentPositionInCandidateKey)));
        }
        return input;
    }


    //Acts as a helper in the elimination process
    public String findNext(String input)
    {
        String closure=new AttributeClosure(input).getAttributeClosure();
        String result="";
        for(int currentPositionInCandidateKey=0;currentPositionInCandidateKey<AttributeClosure.AllAttributeSuperKey(this.attributes).length();currentPositionInCandidateKey++){
            String possibility=String.valueOf(AttributeClosure.AllAttributeSuperKey(this.attributes).charAt(currentPositionInCandidateKey));
            if(!closure.contains(possibility)){
                result=result+possibility;
            }
        }
        return String.valueOf(result.charAt(0));
    }
    //Checks if there are common elements between primary attributes and dependant attributes
    // and then generates more candidate keys accordingly by replacement process
    public Set<String>getAllCandidateKeys()
    {
        Set<String>candidateKeys=new TreeSet<String>();
        String firstCK=this.getACandidateKey();
        candidateKeys.add(firstCK);
        int currentSize;
        do{//CURRENT_MAX_CANDIDATE_KEY_SIZE is used to ensure all the attributes that can be replaced is replaced
            int CURRENT_MAX_CANDIDATE_KEY_SIZE=CandidateKeyTest.findLargestCKSize(candidateKeys);
            currentSize=candidateKeys.size();
            candidateKeys=generateNewCandidateKeys(candidateKeys,CURRENT_MAX_CANDIDATE_KEY_SIZE);
        }while(candidateKeys.size()!=currentSize);
        return candidateKeys;
    }

    public Set<String>generateNewCandidateKeys(Set<String>current,int CURRENT_MAX_CANDIDATE_KEY_SIZE)
    {
        //performs the replacement process
        for(String CandidateKey:current)
        {
            List<String>intersection=attributeSetOperations.findIntersection();
            int currentPositionInCandidateKey=0;
            while(intersection.size()!=0)
            {
                List<Pair<String,String>>Dependees=new DependencyHandler().getDependeeFromDependant(intersection);
                //break the loop when it starts to produce redundant superkeys
                if(generateNewCandidateKeys(Dependees,CandidateKey,current,currentPositionInCandidateKey++,CURRENT_MAX_CANDIDATE_KEY_SIZE))break;
                intersection=attributeSetOperations.findIntersection();
            }
        }
        return current;
    }
    
    private boolean generateNewCandidateKeys(List<Pair<String,String>>Dependees,String CandidateKey,Set<String>current,int currentPositionInCandidateKey,int CURRENT_MAX_CANDIDATE_KEY_SIZE){
        boolean found=false;
        for(Pair<String,String> dependee:Dependees)
        {
            String currentCandidateKey=CandidateKey.replace(dependee.first, dependee.second);
            if(new CandidateKeyTest(this.attributes).isCandidateKey(currentCandidateKey) && !currentCandidateKey.equals(CandidateKey))
            {
                current.add(currentCandidateKey);
                if(currentPositionInCandidateKey==CURRENT_MAX_CANDIDATE_KEY_SIZE)
                {
                    currentPositionInCandidateKey=attributeSetOperations.fixPSSIntersectsDA(dependee);//going to 0 indicates restarting or resetting of counter
                }
            }
            else{
                found=true;
            }
        }
        return found;
    }

}
