// Names: Brogan Clements, Dominick DiMaggio, Ishaan Patel
// Pledge: I pledge my honor that I have abided by the Stevens Honor System.

#include <assert.h>
#include <limits.h>
#include <math.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define ALPHABET_LENGTH    26
#define OPERATION_BUF_SIZE  7 /* Large enough to cover the word 'search' and '\0' */
#define NAME_BUF_SIZE      22

/* Basic trie node -- also, keep track of number of nodes below this one. */
typedef struct node {
    int prefix_count;
    /* Allocate +1 for the the pointer to the end-of-string marker. Needed
       for the search feature. */
    struct node *children[ALPHABET_LENGTH + 1];
} trie_node;

void init_node(trie_node*);

// Initalizes a node with null values in the child
void init_node(trie_node *n) {
    n->prefix_count = 0;
    
    for(int i = 0; i <= ALPHABET_LENGTH; i++) {
        n->children[i] = NULL;
    }
}

// Adds a word into the trie node letter by letter
void do_add(char *word, trie_node *n) {
    while(*word != '\0') {
        // if the child node is null, allocate space for it and make it
        if(n->children[*word - 'a'] == NULL) {
            n->children[*word - 'a'] = (trie_node*) malloc(sizeof(trie_node));
            init_node(n->children[*word - 'a']);
        }

        // go to the child node, update count and go to next letter
        n = n->children[*word - 'a'];
        n->prefix_count++;
        word++;
    }

    // Add end-of-string marker
    if(n->children[ALPHABET_LENGTH] == NULL) {
            n->children[ALPHABET_LENGTH] = (trie_node*) malloc(sizeof(trie_node));
            init_node(n->children[ALPHABET_LENGTH]);
    }

    n = n->children[ALPHABET_LENGTH];
    n->prefix_count++;
}

// the find function
int do_find(char *word, trie_node *n) {
    while(*word != '\0') {
        // Not in list
        if(n->children[*word - 'a'] == NULL) {
            return 0;
        }

        n = n->children[*word - 'a'];
        word++;
    }

    return n->prefix_count;
}

// the search function
int do_search(char *word, trie_node *n) {
    while (*word != '\0') {
        // if letter don't exist, return 0
        if (n->children[*word - 'a'] == NULL) 
            return 0;
        
        n = n->children[*word - 'a'];
        word++;
    }

    // so if the complete word is there, then the node for the null character should exist
    // and if it does exist, then it's prefix count would already be at least 1
    // so you just need to check if its null
    if (n->children[ALPHABET_LENGTH] == NULL)
        return 0;
    else
        return 1;
}

// we have to recursively traverse the trie node and free everything inside it
void free_helper(trie_node *n) {
    for(int i = 0; i <= ALPHABET_LENGTH; i++) {
        if(n->children[i] != NULL) {
            free_helper(n->children[i]);
        }
    }
    free(n);
}

void free_memory(trie_node *n) {
    for(int i = 0; i <= ALPHABET_LENGTH; i++) {
        if(n->children[i] != NULL) {
            free_helper(n->children[i]);
        }
    }
}

/*
 * Complete the contacts function below.
 */

/*
 * Please store the size of the integer array to be returned in result_count pointer. For example,
 * int a[3] = {1, 2, 3};
 *
 * *result_count = 3;
 *
 * return a;
 *
 */
 
void contacts(int queries) {
    /*
     * Write your code here.
     */
    
    // initalize root of the trie node
    trie_node root;
    init_node(&root);

    // Allocate space for the int array
    int *a = (int*)malloc(queries * sizeof(int));
    
    int find_count = 0;
    int search_count = 0;

    char op[OPERATION_BUF_SIZE], word[NAME_BUF_SIZE];
    //this uses the query value then decrements it
    while(queries-- > 0) {
        scanf("%s %s", op, word);

        if(strcmp(op, "add") == 0) {
            do_add(word, &root);
        }
        else if(strcmp(op, "find") == 0) {
            a[find_count++] = do_find(word, &root);
        }
        else if (strcmp(op, "search") == 0)
            //uses search count then increments it
            a[search_count++] = do_search(word, &root);
        else {
            fprintf(stderr, "Input wrong type\n");
        }
    }

    free_memory(&root);

    for(int i = 0; i < search_count + find_count; i++) {
        printf("%d\n", a[i]);
    }

    free(a);
}

int main() {
    int queries;
    scanf("%d", &queries);

    int result_count;
    contacts(queries);

    return 0;
}