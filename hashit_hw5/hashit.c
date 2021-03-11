#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#define TABLE_SIZE 101

typedef struct {
    char *keys[TABLE_SIZE];
    int num_keys;
} hash_set;

// wipes the table by freeing all values in set
void clear_table(hash_set *set) {
    for (int i = 0; i < TABLE_SIZE; i++) {
        if (set->keys[i] != NULL) {
            free(set->keys[i]);
            set->keys[i] = NULL;
        }
    }
    set->num_keys = 0;
}

// hash is 19 * (ASCII(a1)*1 + ... + ASCII(an)*n)
// what in the actual hell is this hash
int hash(char *key) {
    int h = 0;
    int i = 1;
    while (*key != '\0' && *key != '\n') {
        h += (int)*key * i;
        ++i;
        ++key;
    }

    return (19 * h) % TABLE_SIZE;
}

// Determines if something has already been placed in the table
// Have to check all in the event something was initially bounced,
// then the occupant is deleted
int exists(hash_set *set, char *key) {
    int h = hash(key);
    if (set->keys[h] != NULL && strcmp(key, set->keys[h]) == 0)
        return 1;
    
    int pos = 0;
    for (int j = 1; j < 20; j++) {
        pos = (h + j * j + 23 * j) % 101;
        if (set->keys[pos] != NULL && strcmp(key, set->keys[pos]) == 0)
            return 1;
    }

	return 0;
}

// it inserts stuff
// assume after 20 entries that you cant insert
// (hash(key) + j^2 + 23^j) % 101 for j=1,...,19 for collisions
int insert_key(hash_set *set, char *key) {
    if(exists(set, key))
        return 0;

    //printf("Inserting %s", key);
    int h = hash(key);
    if (set->keys[h] == NULL) {
        set->keys[h] = (char *)malloc((strlen(key) + 1) * sizeof(char));
        strcpy(set->keys[h], key);
        set->num_keys += 1;
        return 1;
    }

    int pos = 0;
    for (int j = 1; j < 20; j++) {
        pos = (h + j * j + 23 * j) % 101;
        if (set->keys[pos] == NULL) {
            set->keys[pos] = (char *)malloc((strlen(key) + 1) * sizeof(char));
            strcpy(set->keys[pos], key);
            set->num_keys += 1;
            return 1;
        }
    }
    return 0;
}

int delete_key(hash_set *set, char *key) {
    int h = hash(key);
    if (set->keys[h] != NULL && strcmp(key, set->keys[h]) == 0) { // somethings in there
            free(set->keys[h]);
            set->keys[h] = NULL;
            set->num_keys -= 1;
            return 1;
    }
    else { // traverse hashes via collision formula
        int pos = -1;
        for (int j = 1; j < 20; j++) {
            pos = (h + j * j + 23 * j) % 101;
            if (set->keys[pos] != NULL &&
                strcmp(key, set->keys[pos]) == 0) { // if it matches
                free(set->keys[pos]);
                set->keys[pos] = NULL;
                set->num_keys -= 1;
                return 1;
            }
        }
    }

    return 0;
}

// it prints stuff, what else am i gonna say
void display_keys(hash_set *set) {
    printf("%d\n", set->num_keys);
    for (int i = 0; i < TABLE_SIZE; ++i)
        if (set->keys[i] != NULL)
            printf("%d:%s\n", i, set->keys[i]);
}

int main(){
    // max line size is gonna be 20 cause input
	char buf[20];
    buf[19] = '\0';
    fgets(buf, 20, stdin);
	while(buf[0] == '\n')
        fgets(buf, 20, stdin);

	int t = atoi(buf);
	for(int testcase=0; testcase<t; testcase++){
		fgets(buf, 20, stdin);
		while(buf[0] == '\n')
            fgets(buf, 20, stdin);

        int n = atoi(buf); // number of operations

        // Initialize hash set
        hash_set hs;
		for (int ii = 0; ii < TABLE_SIZE; ii++) {
            hs.keys[ii] = NULL;
        }
        hs.num_keys = 0;

        for (int j = 0; j < n; ++j) {
            fgets(buf, 20, stdin);
            while(buf[0] == '\n')
                fgets(buf, 20, stdin);

            if (buf[0] == 'A') { // Add (ha)shit
                insert_key(&hs, buf + 4);
            } else { // Delete (ha)shit
                delete_key(&hs, buf + 4);
            }

        }

		display_keys(&hs);
        clear_table(&hs);
	}
	return 0;
}

/*
⠄⠄⠄⠄⠄⠄⠄⠄⠄⢀⣤⣶⣿⣿⣿⣿⣿⣿⣿⣶⣄⠄⠄⠄⠄⠄⠄⠄⠄⠄
⠄⠄⠄⠄⠄⠄⠄⢀⣴⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⠄⠄⠄⠄⠄⠄⠄⠄
⠄⠄⠄⠄⠄⠄⢀⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣧⠄⠄⠄⠄⠄⠄⠄
⠄⠄⠄⠄⠄⣴⡿⠛⠉⠁⠄⠄⠄⠄⠈⢻⣿⣿⣿⣿⣿⣿⣿⠄⠄⠄⠄⠄⠄⠄
⠄⠄⠄⠄⢸⣿⡅⠄⠄⠄⠄⠄⠄⠄⣠⣾⣿⣿⣿⣿⣿⣿⣿⣷⣶⣶⣦⠄⠄⠄
⠄⠄⠄⠄⠸⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣇⠄⠄
⠄⠄⠄⠄⠄⢸⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⠄⠄
⠄⠄⠄⠄⠄⢸⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⠄⠄
⠄⠄⠄⠄⠄⢸⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⠄⠄
⠄⠄⠄⠄⠄⢸⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⠄⠄
⠄⠄⠄⠄⠄⠘⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⠄⠄
⠄⠄⠄⠄⠄⠄⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡟⠛⠛⠛⠃⠄⠄
⠄⠄⠄⠄⠄⣼⠄⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⠄⠄⠄⣿⠄⠄⠄⠄⠄⠄
⠄⠄⠄⠄⢰⣿⣿⠄⠄⠄⠄amogus⠄⠄⠄⣿⣿⣿⣿⡇⠄⠄⠄⠄⠄
⠄⠄⠄⢀⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⠄⠄⠄⠄⠄
⠄⠄⠄⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⢻⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡆⠄⠄⠄⠄
⠄⠄⢠⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⠃⠄⢿⣿⣿⣿⣿⣿⣿⣿⣿⣿⠇⠄⠄⠄⠄
⠄⠄⢸⣿⣿⣿⣿⣿⣿⣿⡿⠟⠁⠄⠄⠄⠻⣿⣿⣿⣿⣿⣿⣿⡿⠄⠄⠄⠄⠄
⠄⠄⢸⣿⣿⣿⣿⣿⡿⠋⠄⠄⠄⠄⠄⠄⠄⠙⣿⣿⣿⣿⣿⣿⡇⠄⠄⠄⠄⠄
⠄⠄⢸⣿⣿⣿⣿⣿⣧⡀⠄⠄⠄⠄⠄⠄⠄⢀⣾⣿⣿⣿⣿⣿⡇⠄⠄⠄⠄⠄
⠄⠄⢸⣿⣿⣿⣿⣿⣿⣿⡄⠄⠄⠄⠄⠄⠄⣿⣿⣿⣿⣿⣿⣿⣷⠄⠄⠄⠄⠄
⠄⠄⠸⣿⣿⣿⣿⣿⣿⣿⣷⠄⠄⠄⠄⠄⢰⣿⣿⣿⣿⣿⣿⣿⣿⠄⠄⠄⠄⠄
⠄⠄⠄⢿⣿⣿⣿⣿⣿⣿⡟⠄⠄⠄⠄⠄⠸⣿⣿⣿⣿⣿⣿⣿⠏⠄⠄⠄⠄⠄
⠄⠄⠄⠈⢿⣿⣿⣿⣿⠏⠄⠄⠄⠄⠄⠄⠄⠙⣿⣿⣿⣿⣿⠏⠄⠄⠄⠄⠄⠄
⠄⠄⠄⠄⠘⣿⣿⣿⣿⡇⠄⠄⠄⠄⠄⠄⠄⠄⣿⣿⣿⣿⡏⠄⠄⠄⠄⠄⠄⠄
⠄⠄⠄⠄⠄⢸⣿⣿⣿⣧⠄⠄⠄⠄⠄⠄⠄⢀⣿⣿⣿⣿⡇⠄⠄⠄⠄⠄⠄⠄
⠄⠄⠄⠄⠄⣸⣿⣿⣿⣿⣆⠄⠄⠄⠄⠄⢀⣾⣿⣿⣿⣿⣿⣄⠄⠄⠄⠄⠄⠄
⠄⣀⣀⣤⣾⣿⣿⣿⣿⡿⠟⠄⠄⠄⠄⠄⠸⣿⣿⣿⣿⣿⣿⣿⣷⣄⣀⠄⠄⠄
⠸⠿⠿⠿⠿⠿⠿⠟⠁⠄⠄⠄⠄⠄⠄⠄⠄⠄⠉⠉⠛⠿⢿⡿⠿⠿⠿⠃⠄⠄
*/