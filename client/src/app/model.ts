export interface Item {
    
    itemId: number
    photoUrl: Blob
    description: string
    price: number
    datePurchased: Date
    timeWorn: number
    costPerWear: number
    category: string
}


export interface DialogData {
    itemId: number
    reason: string
}

export interface analysisResult {
    itemDescription: string;
  }

export interface nextUrl {
    nextUrl: string;
  }

export interface redirectUrl {
    redirectUrl: string;
  }

export interface OOTDfiles{
    folderId: string;
    files: string[];
}