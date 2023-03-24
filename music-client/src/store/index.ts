import { createPinia } from 'pinia';
import useConfigureStore from "./configure";
import useUserStore from "./user";
import useSongStore from "./song";


function useStore(){
    return {
       configure:useConfigureStore(),
       user:useUserStore(),
       song:useSongStore(),
     }
}
export default useStore
export const pinia = createPinia()
