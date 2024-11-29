function stringToSeed(seed) {
    let hash = 0;
    for (let i = 0; i < seed.length; i++) {
        const char = seed.charCodeAt(i);
        hash = (hash << 5) - hash + char; 
        hash |= 0; 
    }
    return hash;
}

export default function randomShuffle(seed, n){
    let shuffledArray = Array.from({ length: n }, (_, index) => index + 1);

    let seedValue = stringToSeed(seed);

    for (let i = shuffledArray.length - 1; i > 0; i--) {
        seedValue = (seedValue * 16807) % 2147483647; 
        let randomIndex = Math.abs(seedValue) % (i + 1);

        [shuffledArray[i], shuffledArray[randomIndex]] = [shuffledArray[randomIndex], shuffledArray[i]];
    }

    return shuffledArray;
}
