import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { firstValueFrom, lastValueFrom } from 'rxjs';

const get_url = "http://localhost:8080/api/item"
// http://localhost:8080/api?category=top
const get_list_category_url = "http://localhost:8080/api"

@Injectable({
  providedIn: 'root'
})
export class ItemService {

  constructor(private httpClient: HttpClient) { }

  getItemById(itemId: number) {

    const get_item_url = get_url + '/' + itemId

    console.log(get_item_url)
    return firstValueFrom(this.httpClient.get<any>(get_item_url))
  }

      // query for list of items with category, fetch from: http://localhost:8080/api?category=top?user=celine

  getItemListByCategory(category: string, userName: string) {

    // set up queryparams 
    const queryParams = new HttpParams()
                        .set("category", category)
                        .set("user", userName);

    return firstValueFrom(this.httpClient.get<any>(get_list_category_url, { params: queryParams } ))

  }

  // http://localhost:8080/api/item/id
  increaseTimeWorn(itemId: number){
    console.log("to increase time worn of item " + itemId)
    const update_item_url = get_url + '/' + itemId
    const body = { timeWorn: 1 }
    
    return firstValueFrom(this.httpClient.put<any>(update_item_url, body))

  }


}