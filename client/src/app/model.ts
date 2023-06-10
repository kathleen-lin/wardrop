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

export interface ApiResponse {
    message: string;
    photoUrl: string;
  }