import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { firstValueFrom, lastValueFrom } from 'rxjs';

const get_url = "http://localhost:8080/api/item"
// http://localhost:8080/api?category=top
const get_list_category_url = "http://localhost:8080/api"
const get_analysis_url = "http:localhost8080/api/analyse"

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

  // post mapping http://localhost:8080/api/item/id
  removeItem(itemId: number, reason: string){

    const header = new HttpHeaders().set('Content-Type', 'application/json')
    const removal_url = get_url + '/' + itemId
    const removalReason = { reason: reason }; 

    return firstValueFrom(this.httpClient.post<any>(removal_url, removalReason, {headers: header}))
    
  }

  uploadImage(fd: FormData) {


    return firstValueFrom(this.httpClient.post('http://localhost:8080/api/uploadImage', fd))
       
  }

  analyseImage(fileName: string) {
    const queryParams = new HttpParams()
                        .set("i", fileName)
  
    return firstValueFrom(this.httpClient.get('http://localhost:8080/api/analyse', { params: queryParams }))
    
  }

  postItem (fd: FormData) {
    
    return firstValueFrom (this.httpClient.post('http://localhost:8080/api/upload/details', fd))
  }

  Gdrive(user: string) {
    const queryParams = new HttpParams()
                        .set("user", user)
    return firstValueFrom(this.httpClient.get('http://localhost:8080/api/drive', { params: queryParams }))
  }

  getAuthUrl(url: string) {
    return firstValueFrom(this.httpClient.get(url))
  }

  getOOTDdrive(user:string) {
    const queryParams = new HttpParams()
    .set("user", user)
    return firstValueFrom(this.httpClient.get('http://localhost:8080/api/drive/home', { params: queryParams}))
  }
}