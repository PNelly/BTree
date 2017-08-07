/**
 * Driver class for managing a collection of caches
 *
 * @author Michael Burke
 * CS321 Summer 2017
 */
public class CacheManager {

    public int cacheLevels;
    public Cache[] caches;
    private Cache cache;


    public CacheManager(int cacheLevels) {
        this.cacheLevels = cacheLevels;
        caches = new Cache[cacheLevels];
    }
    /**
     addCache takes in the level and size of a cache to create, and adds
     it into the array of caches named "caches"
     */
    public void addCache(int level, int size) {
        caches[level] = new Cache(size);
        if (cacheLevels == 1)
            cache = caches[0];
        System.out.printf("Level %d cache with %d entries has been created\n", level+1, size);
    }

    public void addToCache(BTreeNode node) {
        cache.addObject(node);
    }

    public void updateNode(BTreeNode node) {
        cache.updateNode(node);
    }

    public void updateChildren(int[] children, int newParent) {
        for (int child : children) {
            if (child != -1)
                cache.updateChildNode(child, newParent);
        }
    }

    public BTreeNode searchCaches(int byteAddress) {
        Node node = cache.findNode(byteAddress);
        if (node != null) {
            cache.moveToFront(node);
            return node.getValue();
        } else {
            return null;
        }
    }

    public void dumpCacheToFile() {

    }

    /**
     searchCaches takes in an object and iterates over each cache level,
     and either adding it into the cache or moving it to the front depending
     on whether or not its been found.
     */
    public void searchCaches(BTreeNode data) {
        boolean found = false;
        for (Cache cache : caches) {
            if (found) {
                Node node = cache.findNode(data);
                if (node != null) {
                    cache.moveToFront(node);
                } else {
                    cache.addObject(data);
                }
            } else {
                found = cache.getObject(data);
            }
        }

    }

    public void printRatios() {
        int globalHit = 0;
        for (int i = 0; i < caches.length; i++) {
            globalHit += caches[i].getHits();
        }
        System.out.printf("\nThe number of global references: %d\n", caches[0].getRefs());
        System.out.printf("The number of global cache hits: %d\n", globalHit);
        System.out.printf("The global hit ratio: %s\n", String.valueOf((float)globalHit/caches[0].getRefs()));

        for (int i = 0; i < caches.length; i++) {
            System.out.printf("\nThe number of Level %d references: %d\n", i+1, caches[i].getRefs());
            System.out.printf("The number of Level %d cache hits: %d\n", i+1, caches[i].getHits());
            System.out.printf("The Level %d hit ratio: %s\n", i+1, String.valueOf((float)caches[i].getHits()/caches[i].getRefs()));
        }
    }


}